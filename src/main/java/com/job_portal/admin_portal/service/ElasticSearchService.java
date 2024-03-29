package com.job_portal.admin_portal.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job_portal.admin_portal.entity.JobPost;
import com.job_portal.admin_portal.request.JobPostSearchRequest;
import com.job_portal.admin_portal.request.RangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {
        private final ElasticsearchClient esRestClient;
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

        /**
         * Match query to match field with the list of values.
         *
         * @param values - list of values to match
         * @param field - field to match
         * @return Query
         */
        private Query matchQuery(List<String> values, String field) {
            List<Query> queries = new ArrayList<>();
            for(String value: values){
                Query matchQuery = QueryBuilders.match()
                        .field( field)
                        .query(value)
                        .build()._toQuery();
                queries.add(matchQuery);
            }
            return QueryBuilders.bool().should(queries).build()._toQuery();
        }

        /**
         * Query to match the prefix phrase
         *
         * @param values - list of values to match
         * @param field - field to match
         * @return Query
         */
        private Query matchPhrasePrefixQuery(List<String> values, String field) {
            List<Query> queries = new ArrayList<>();
            for(String value: values){
                Query matchQuery = QueryBuilders.matchPhrasePrefix()
                        .field( field)
                        .query(value)
                        .build()._toQuery();
                queries.add(matchQuery);
            }
            return QueryBuilders.bool().should(queries).build()._toQuery();
        }

        /**
         * Query to match the range of values. ie Experience and Salary range
         *
         * @param values - List of the values to match
         * @param field - Field to match
         * @return Query
         */
        private Query boolQuery(List<RangeRequest> values, String field) {
            List<Query> queries = new ArrayList<>();
            for(RangeRequest value: values){
                if(value.getMax() == null) {
                    Query minQuery = QueryBuilders.range().field("min"+field).gte(JsonData.of(value.getMin())).build()._toQuery();
                    Query maxQuery = QueryBuilders.range().field("max"+field).gte(JsonData.of(value.getMin())).build()._toQuery();
                    Query boolQuery = QueryBuilders.bool().must(minQuery).must(maxQuery).build()._toQuery();
                    queries.add(boolQuery);
                } else {
                    Query minQuery = QueryBuilders.range().field("min"+field).lte(JsonData.of(value.getMax())).build()._toQuery();
                    Query maxQuery = QueryBuilders.range().field("max"+field).gte(JsonData.of(value.getMin())).build()._toQuery();
                    Query boolQuery = QueryBuilders.bool().must(minQuery).must(maxQuery).build()._toQuery();
                    queries.add(boolQuery);
                }
            }
            return QueryBuilders.bool().should(queries).build()._toQuery();
        }


        /**
         * Query to match the posted days.
         *
         * @param keys - posted days to match
         * @return Query
         */
        private Query postedDateQuery(List<String> keys) {
            List<Query> queries = new ArrayList<>();
            for(String key: keys){
                if(key.equals("today"))
                    queries.add(this.todayQuery());
                if (key.equals("yesterday"))
                    queries.add(this.yesterdayQuery());
                if (key.equals("threeDaysAgo"))
                    queries.add(this.threeDaysAgoQuery());
                if (key.equals("weekAgo"))
                    queries.add(this.weekAgoQuery());
                if (key.equals("monthAgo"))
                    queries.add(this.monthAgoQuery());
            }
            return QueryBuilders.bool().should(queries).build()._toQuery();
        }

        /**
         * To search job post with given filter and search values.
         *
         * @param indexName - Name of the index
         * @param request - Request with search values and filters
         * @return Hashmap of job post and grouped values
         */
        @SneakyThrows
        public Map<String, Object> searchJobPosts(String indexName, JobPostSearchRequest request) {
            GetMappingRequest getMappingRequest = new GetMappingRequest.Builder().index(Arrays.asList(indexName)).build();
            GetMappingResponse mappingsResponse = esRestClient.indices().getMapping(getMappingRequest);
            log.info("Mapping {}", mappingsResponse);
//        Matches the keyword with title or position or location or keyskills
            Query multiMatchQuery = QueryBuilders.multiMatch()
                    .fields("title","position",  "location", "keySkills")
                    .query(request.getKeyword())
                    .operator(Operator.Or)
                    .type(TextQueryType.MostFields).build()._toQuery();
//        Expiration date should be greater than or equal to current date
            Query rangeQuery = QueryBuilders.range()
                    .field("expirationDate")
                    .gte(JsonData.of(new Date())).build()._toQuery();
            List<Query> queries = new ArrayList<>();
            queries.add(multiMatchQuery);
            queries.add(rangeQuery);
//        Matches the list of locations
            if(request.getLocations() != null && !request.getLocations().isEmpty()) {
                queries.add(this.matchQuery(request.getLocations(), "location"));
            }
//        Matches the list of positions
            if(request.getPositions() != null && !request.getPositions().isEmpty()) {
                queries.add(this.matchPhrasePrefixQuery(request.getPositions(), "position"));
            }

            //        Matches the list of salary ranges
            if(request.getSalary() != null && !request.getSalary().isEmpty()) {
                queries.add(this.boolQuery(request.getSalary(), "Salary"));
            }

            //        Matches the list of experience ranges
            if(request.getExperience() != null && !request.getExperience().isEmpty()) {
                queries.add(this.boolQuery(request.getExperience(), "Exp"));
            }

            //        Matches the list of posted dates
            if(request.getPostedDates() != null && !request.getPostedDates().isEmpty()) {
                queries.add(this.postedDateQuery(request.getPostedDates()));
            }

            BoolQuery finalBoolQuery = QueryBuilders.bool().must(queries).build();

//        Query to search
            SearchResponse<JobPost> response =  esRestClient.search(s -> s
                            .index(indexName)
                            .query(q -> q
                                    .bool(finalBoolQuery
                                    ))
                            .from(request.getFrom())
                            .size(request.getSize()),
                    JobPost.class);
            List<JobPost> jobPosts =  response.hits().hits().stream()
                    .map(Hit::source).collect(Collectors.toList());
            Map<String, Object> result = new HashMap<>();
            result.put("jobPosts", jobPosts);

            //        Aggregations
            Map<String, Aggregation> map = new HashMap<>();
            map.put("agg_location", this.locationAggregation());
            map.put("agg_position", this.positionAggregation());
            map.put("agg_exp", this.experienceAggregation());
            map.put("agg_salary", this.salaryAggregation());
            map.put("agg_posted_date", this.postedDateAggregation());

            SearchResponse<Void> agg =  esRestClient.search(s -> s
                            .index(indexName)
                            .query(q -> q
                                    .bool(finalBoolQuery
                                    ))
                            .aggregations(map),
                    Void.class);

            List<StringTermsBucket> locList = agg.aggregations().get("agg_location").sterms().buckets().array();
            List<Map<String, Object>> locationResult = new ArrayList<>();
            for(StringTermsBucket bucket: locList){
                Map<String, Object> location = new HashMap<>();
                location.put("key", bucket.key().stringValue());
                location.put("count", bucket.docCount());
                locationResult.add(location);
            }
            List<StringTermsBucket> posList = agg.aggregations().get("agg_position").sterms().buckets().array();
            List<Map<String, Object>> positionResult= new ArrayList<>();
            for(StringTermsBucket bucket: posList){
                Map<String, Object> position = new HashMap<>();
                position.put("key", bucket.key().stringValue());
                position.put("count", bucket.docCount());
                positionResult.add(position);
            }

            Map<String, FiltersBucket> expList = agg.aggregations().get("agg_exp").filters().buckets().keyed();
            List<Map<String, Object>> expResult = new ArrayList<>();
            for (Map.Entry<String,FiltersBucket> entry : expList.entrySet()) {
                Map<String, Object> exp = new HashMap<>();
                if(entry.getKey().contains("-")){
                    exp.put("key", entry.getKey()+" Years");
                    exp.put("min", entry.getKey().split("-")[0]);
                    exp.put("max", entry.getKey().split("-")[1]);
                }else {
                    exp.put("key", entry.getKey()+"+ Years");
                    exp.put("min", entry.getKey());
                }

                exp.put("count", Integer.valueOf(entry.getValue().toString()
                        .replace("FiltersBucket: {\"doc_count\":", "").replace("}", "")));
                expResult.add(exp);
            }

            Map<String, FiltersBucket> salaryList = agg.aggregations().get("agg_salary").filters().buckets().keyed();
            List<Map<String, Object>> salaryResult = new ArrayList<>();
            for (Map.Entry<String,FiltersBucket> entry : salaryList.entrySet()) {
                Map<String, Object> salary = new HashMap<>();
                if(entry.getKey().contains("-")){
                    salary.put("key", entry.getKey()+" Lakhs");
                    salary.put("min", Integer.valueOf(entry.getKey().split("-")[0])*100000);
                    salary.put("max", Integer.valueOf(entry.getKey().split("-")[1])*100000);
                }else {
                    salary.put("key", entry.getKey()+"+ Lakhs");
                    salary.put("min", Integer.valueOf(entry.getKey())*100000);
                }
                salary.put("count", Integer.valueOf(entry.getValue().toString()
                        .replace("FiltersBucket: {\"doc_count\":", "").replace("}", "")));
                salaryResult.add(salary);
            }


            Map<String, FiltersBucket> postedDateList = agg.aggregations().get("agg_posted_date").filters().buckets().keyed();
            List<Map<String, Object>> postedDateResult = new ArrayList<>();
            for (Map.Entry<String,FiltersBucket> entry : postedDateList.entrySet()) {
                Map<String, Object> postedDateData = new HashMap<>();
                postedDateData.put("key", entry.getKey());
                postedDateData.put("count", Integer.valueOf(entry.getValue().toString()
                        .replace("FiltersBucket: {\"doc_count\":", "").replace("}", "")));
                postedDateResult.add(postedDateData);
            }

            result.put("locations", locationResult);
            result.put("positions", positionResult);
            result.put("experiences", expResult);
            result.put("salaries", salaryResult);
            result.put("postedDays", postedDateResult);
            return result;
        }

        /**
         * Suggest job posts based on the search keyword and field.
         *
         * @param indexName - name of the index
         * @param keyword - keyword to be searched.
         * @param field - field to be searched.
         * @return List of suggestions
         */
        @SneakyThrows
        public List<String> suggestJobPosts(String indexName, String keyword, String field)  {
            Query query = QueryBuilders.matchPhrasePrefix().field(field).query(keyword).build()._toQuery();
            SearchResponse<JobPost> result = this.esRestClient.search(s->s.index(indexName).query(query), JobPost.class);
            return result.hits().hits().stream()
                    .map(Hit::source).map(a-> {
                        ObjectMapper m = new ObjectMapper();
                        Map<String,String> jobPost = m.convertValue(a, Map.class);
                        return jobPost.get(field);
                    }).distinct().collect(Collectors.toList());
        }

        /**
         * Aggregate job posts with location
         *
         * @return Aggregation
         */
        private Aggregation locationAggregation(){
            return new Aggregation.Builder()
                    .terms(new TermsAggregation.Builder().field("location.keyword").build())
                    .build();
        }

        /**
         * Aggregate job post with experience ranges
         *
         * @return Aggregation
         */
        private Aggregation experienceAggregation(){
            Query minExp0to2 = QueryBuilders.range().field("minExp").lte(JsonData.of("2")).build()._toQuery();
            Query maxExp0to2 = QueryBuilders.range().field("maxExp").gte(JsonData.of("0")).build()._toQuery();
            Query boolQuery0to2 = QueryBuilders.bool().queryName("0to2").must(minExp0to2).must(maxExp0to2).build()._toQuery();

            Query minExp2to4 = QueryBuilders.range().field("minExp").lte(JsonData.of("4")).build()._toQuery();
            Query maxExp2to4 = QueryBuilders.range().field("maxExp").gte(JsonData.of("2")).build()._toQuery();
            Query boolQuery2to4 = QueryBuilders.bool().queryName("2to4").must(minExp2to4).must(maxExp2to4).build()._toQuery();

            Query minExp4to6 = QueryBuilders.range().field("minExp").lte(JsonData.of("6")).build()._toQuery();
            Query maxExp4to6 = QueryBuilders.range().field("maxExp").gte(JsonData.of("4")).build()._toQuery();
            Query boolQuery4to6 = QueryBuilders.bool().queryName("4to6").must(minExp4to6).must(maxExp4to6).build()._toQuery();

            Query minExp6to8 = QueryBuilders.range().field("minExp").lte(JsonData.of("8")).build()._toQuery();
            Query maxExp6to8 = QueryBuilders.range().field("maxExp").gte(JsonData.of("6")).build()._toQuery();
            Query boolQuery6to8 = QueryBuilders.bool().queryName("6to8").must(minExp6to8).must(maxExp6to8).build()._toQuery();

            Query minExp8to10 = QueryBuilders.range().field("minExp").lte(JsonData.of("10")).build()._toQuery();
            Query maxExp8to10 = QueryBuilders.range().field("maxExp").gte(JsonData.of("8")).build()._toQuery();
            Query boolQuery8to10 = QueryBuilders.bool().queryName("8to10").must(minExp8to10).must(maxExp8to10).build()._toQuery();

            Query minExpGt10 = QueryBuilders.range().field("minExp").gte(JsonData.of("10")).build()._toQuery();
            Query maxExpGt10 = QueryBuilders.range().field("maxExp").gte(JsonData.of("10")).build()._toQuery();
            Query boolQueryGt10 = QueryBuilders.bool().queryName(">10").must(minExpGt10).must(maxExpGt10).build()._toQuery();

            Map<String, Query> queries = new HashMap<>();
            queries.put("0-2", boolQuery0to2);
            queries.put("2-4", boolQuery2to4);
            queries.put("4-6", boolQuery4to6);
            queries.put("6-8", boolQuery6to8);
            queries.put("8-10", boolQuery8to10);
            queries.put("10", boolQueryGt10);


            Buckets<Query> queryBuckets = new Buckets.Builder<Query>().keyed(queries).build();
            FiltersAggregation filtersAggregation = new FiltersAggregation.Builder().filters(queryBuckets).build();
            return new Aggregation.Builder()
                    .filters(filtersAggregation)
                    .build();
        }

        /**
         * Aggregate job posts with the posted days.
         *
         * @return Aggregation
         */
        private Aggregation postedDateAggregation(){
            Map<String, Query> queries = new HashMap<>();
            queries.put("today", this.todayQuery());
            queries.put("yesterday", this.yesterdayQuery());
            queries.put("threeDaysAgo", this.threeDaysAgoQuery());
            queries.put("weekAgo", this.weekAgoQuery());
            queries.put("monthAgo", this.monthAgoQuery());
            Buckets<Query> queryBuckets = new Buckets.Builder<Query>().keyed(queries).build();
            FiltersAggregation filtersAggregation = new FiltersAggregation.Builder().filters(queryBuckets).build();
            return new Aggregation.Builder()
                    .filters(filtersAggregation)
                    .build();
        }

        /**
         * Query to return job posts that are posted today.
         *
         * @return Query
         */
        private Query todayQuery(){
            return QueryBuilders.range()
                    .field("postedDate")
                    .gte(JsonData.of(sdf.format(new Date())))
                    .lte(JsonData.of(sdf.format(new Date()))).build()._toQuery();
        }

        /**
         * Query to return job posts that are posted yesterday.
         *
         * @return Query
         */
        private Query yesterdayQuery() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            return QueryBuilders.range()
                    .field("postedDate")
                    .gte(JsonData.of(sdf.format(calendar.getTime())))
                    .lte(JsonData.of(sdf.format(calendar.getTime()))).build()._toQuery();
        }

        /**
         * Query to return job posts that are posted three days ago.
         *
         * @return Query
         */
        private Query threeDaysAgoQuery(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -3);
            Date threeDago = calendar.getTime();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -7);
            Date weekAgo = calendar.getTime();
            return QueryBuilders.range()
                    .field("postedDate")
                    .lte(JsonData.of(sdf.format(threeDago)))
                    .gte(JsonData.of(sdf.format(weekAgo))).build()._toQuery();
        }

        /**
         * Query to return job posts that are posted a week ago.
         *
         * @return Query
         */
        private Query weekAgoQuery(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -7);
            Date weekAgo = calendar.getTime();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -30);
            Date monthAgo = calendar.getTime();
            return QueryBuilders.range()
                    .field("postedDate")
                    .lte(JsonData.of(sdf.format(weekAgo)))
                    .gte(JsonData.of(sdf.format(monthAgo))).build()._toQuery();
        }

        /**
         * Query to return job posts that are posted a month ago.
         *
         * @return Query
         */
        private Query monthAgoQuery(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -30);
            return QueryBuilders.range()
                    .field("postedDate")
                    .lte(JsonData.of(sdf.format(calendar.getTime()))).build()._toQuery();
        }

        /**
         * Aggregation job posts with salary ranges
         *
         * @return Aggregation
         */
        private Aggregation salaryAggregation(){
            Query minExp0to2 = QueryBuilders.range().field("minSalary").lte(JsonData.of("200000")).build()._toQuery();
            Query maxExp0to2 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("0")).build()._toQuery();
            Query boolQuery0to2 = QueryBuilders.bool().queryName("0to2").must(minExp0to2).must(maxExp0to2).build()._toQuery();

            Query minExp2to4 = QueryBuilders.range().field("minSalary").lte(JsonData.of("400000")).build()._toQuery();
            Query maxExp2to4 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("200000")).build()._toQuery();
            Query boolQuery2to4 = QueryBuilders.bool().queryName("2to4").must(minExp2to4).must(maxExp2to4).build()._toQuery();

            Query minExp4to6 = QueryBuilders.range().field("minSalary").lte(JsonData.of("600000")).build()._toQuery();
            Query maxExp4to6 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("400000")).build()._toQuery();
            Query boolQuery4to6 = QueryBuilders.bool().queryName("4to6").must(minExp4to6).must(maxExp4to6).build()._toQuery();

            Query minExp6to8 = QueryBuilders.range().field("minSalary").lte(JsonData.of("800000")).build()._toQuery();
            Query maxExp6to8 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("600000")).build()._toQuery();
            Query boolQuery6to8 = QueryBuilders.bool().queryName("6to8").must(minExp6to8).must(maxExp6to8).build()._toQuery();

            Query minExp8to10 = QueryBuilders.range().field("minSalary").lte(JsonData.of("1000000")).build()._toQuery();
            Query maxExp8to10 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("800000")).build()._toQuery();
            Query boolQuery8to10 = QueryBuilders.bool().queryName("8to10").must(minExp8to10).must(maxExp8to10).build()._toQuery();

            Query minExpGt10 = QueryBuilders.range().field("minSalary").gte(JsonData.of("1000000")).build()._toQuery();
            Query maxExpGt10 = QueryBuilders.range().field("maxSalary").gte(JsonData.of("1000000")).build()._toQuery();
            Query boolQueryGt10 = QueryBuilders.bool().queryName(">10").must(minExpGt10).must(maxExpGt10).build()._toQuery();

            Map<String, Query> queries = new HashMap<>();
            queries.put("1-2", boolQuery0to2);
            queries.put("2-4", boolQuery2to4);
            queries.put("4-6", boolQuery4to6);
            queries.put("6-8", boolQuery6to8);
            queries.put("8-10", boolQuery8to10);
            queries.put("10", boolQueryGt10);

            Buckets<Query> queryBuckets = new Buckets.Builder<Query>().keyed(queries).build();
            FiltersAggregation filtersAggregation = new FiltersAggregation.Builder().filters(queryBuckets).build();
            return new Aggregation.Builder()
                    .filters(filtersAggregation)
                    .build();
        }

        /**
         * Aggregate the job posts with position
         *
         * @return Aggregation
         */
        private Aggregation positionAggregation() {
            return new Aggregation.Builder()
                    .terms(new TermsAggregation.Builder().field("position.keyword").build())
                    .build();
        }
}
