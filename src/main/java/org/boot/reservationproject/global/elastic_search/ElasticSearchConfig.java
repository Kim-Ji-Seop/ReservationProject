package org.boot.reservationproject.global.elastic_search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.boot.reservationproject.domain.search.repository.FacilitySearchRepository;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = FacilitySearchRepository.class)
public class ElasticSearchConfig {
  @Bean
  public ElasticsearchClient elasticsearchClient() {
    Header[] defaultHeaders = new Header[]{
        new BasicHeader("Authorization", "ApiKey LXhnUnJKQUJxeGRqd00tOFlmX1c6V0J3SmVpT3JRYnlxYm9YdjN1VFFpUQ==")
    };

    RestClientBuilder builder = RestClient.builder(
            new HttpHost("26cc4e3a704c4790adb3734900adbb02.us-east-2.aws.elastic-cloud.com", 443, "https"))
        .setDefaultHeaders(defaultHeaders);

    RestClient restClient = builder.build();
    return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
  }

}
