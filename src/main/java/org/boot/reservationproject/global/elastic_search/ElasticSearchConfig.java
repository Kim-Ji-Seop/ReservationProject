package org.boot.reservationproject.global.elastic_search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.boot.reservationproject.domain.search.repository.FacilitySearchRepository;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = FacilitySearchRepository.class)
public class ElasticSearchConfig {
  @Value("${spring.elasticsearch.rest.username}")
  private String username;

  @Value("${spring.elasticsearch.rest.password}")
  private String password;

  @Value("${spring.elasticsearch.rest.uris}")
  private String uri;

  @Value("${spring.elasticsearch.rest.ssl.trust-store-location}")
  private String certPath;

  @Bean
  public ElasticsearchClient elasticsearchClient() throws Exception {
    FileSystemResource resource = new FileSystemResource(certPath);
    InputStream is = resource.getInputStream();

    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    trustStore.load(null, null);
    trustStore.setCertificateEntry("ca", factory.generateCertificate(is));

    SSLContext sslContext = SSLContextBuilder.create()
        .loadTrustMaterial(trustStore, null)
        .build();

    // Basic 인증 정보 설정
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(username, password));

    RestClientBuilder builder = RestClient.builder(
            HttpHost.create(uri))
        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
            .setSSLContext(sslContext)
            .setDefaultCredentialsProvider(credentialsProvider));

    RestClient restClient = builder.build();
    return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
  }

}
