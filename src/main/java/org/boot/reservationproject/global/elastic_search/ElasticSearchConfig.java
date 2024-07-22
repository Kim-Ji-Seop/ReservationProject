package org.boot.reservationproject.global.elastic_search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
public class ElasticSearchConfig {
  @Value("${spring.elasticsearch.rest.username}")
  private String username;

  @Value("${spring.elasticsearch.rest.password}")
  private String password;

  @Value("${spring.elasticsearch.rest.uris}")
  private String uri;


  @Bean
  public ElasticsearchClient elasticsearchClient() throws Exception {
    // Basic 인증 정보 설정
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(username, password));

    RestClientBuilder builder = RestClient.builder(
            HttpHost.create(uri))
        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
            .setDefaultCredentialsProvider(credentialsProvider));

    RestClient restClient = builder.build();

    // ObjectMapper에 JavaTimeModule 등록
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

    ElasticsearchClient client = new ElasticsearchClient(new RestClientTransport(restClient, jsonpMapper));

    // 인덱스 생성
    createIndexIfNotExists(client);

    return client;
  }
  private void createIndexIfNotExists(ElasticsearchClient client) throws Exception {
    // settings.json 파일 읽기
    Resource resource = new ClassPathResource("settings.json");
    InputStream is = resource.getInputStream();
    String settingsJson = new String(is.readAllBytes());

    // 인덱스 생성 요청
    CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
        .index("facilities")
        .withJson(new ByteArrayInputStream(settingsJson.getBytes(StandardCharsets.UTF_8)))
        .build();

    // 인덱스가 없을 경우에만 생성
    if (!client.indices().exists(b -> b.index("facilities")).value()) {
      CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
      if (!createIndexResponse.acknowledged()) {
        throw new RuntimeException("Failed to create index 'facilities'");
      }
    }
  }
}
