package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.pulsar;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.pulsar")
public class PulsarProperties {

    private List<PulsarClientProperties> clients;

    @Getter
    @Setter
    @ToString
    public static class PulsarClientProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}PulsarClient"</code>
         */
        private String beanName;
        /**
         * The default is "pulsar://localhost:6650";
         */
        private String serviceUrl = "pulsar://localhost:6650";
        /**
         * The listenerName that the broker will return the corresponding
         * `advertisedListener`.
         */
        private String listenerName;
        /**
         * Name of the authentication plugin.
         */
        private String authPluginClassName;
        /**
         * String represents parameters for the authentication plugin.
         * 
         * <p>
         * <h3>Example</h3> key1:val1,key2:val2
         */
        private String authParamsString;
        /**
         * Map represents parameters for the authentication plugin.
         * 
         * <p>
         * If present, the field {@code authParamsString} will be ignored.
         */
        private Map<String, String> authParams;
        /**
         * Operation timeout.
         * <p>
         * The default is {@code 30s}.
         */
        private Duration operationTimeout;
        /**
         * Interval between each stats info.
         * <p>
         * The default is {@code 60s}.
         * <p>
         * Must be longer then {@code 1s}.
         */
        private Duration statsInterval;
        /**
         * The number of threads used for handling connections to brokers.
         * <p>
         * The default is {@code 1}.
         */
        private Integer numIoThreads;
        /**
         * The number of threads used for handling message listeners.
         * <p>
         * The default is {@code 1}.
         */
        private Integer numListenerThreads;
        /**
         * The max number of connection that the client library will open to a single
         * broker.
         * <p>
         * The default is {@code 1}.
         */
        private Integer connectionsPerBroker;
        /**
         * Whether to use TCP no-delay flag on the connection to disable Nagle
         * algorithm.
         * <p>
         * The default is {@code true}.
         */
        private Boolean useTcpNoDelay;
        /**
         * Path to the trusted TLS certificate file.
         */
        private String tlsTrustCertsFilePath;
        /**
         * Whether the Pulsar client accepts untrusted TLS certificate from broker.
         * <p>
         * The default is {@code false}.
         */
        private Boolean tlsAllowInsecureConnection;
        /**
         * Whether to enable TLS hostname verification.
         * <p>
         * The default is {@code false}.
         */
        private Boolean tlsHostnameVerificationEnable;
        /**
         * If Tls is enabled, whether use KeyStore type as tls configuration parameter.
         * <p>
         * The default is {@code false}.
         */
        private Boolean useKeyStoreTls;
        /**
         * The name of the security provider used for SSL connections. Default value is
         * the default security provider of the JVM.
         */
        private String sslProvider;
        /**
         * The file format of the trust store file.
         */
        private String tlsTrustStoreType;
        /**
         * The location of the trust store file.
         */
        private String tlsTrustStorePath;
        /**
         * The store password for the key store file.
         */
        private String tlsTrustStorePassword;
        /**
         * A list of cipher suites.
         * <p>
         * This is a named combination of authentication, encryption, MAC and key
         * exchange algorithm used to negotiate the security settings for a network
         * connection using TLS or SSL network protocol.
         * <p>
         * By default all the available cipher suites are supported.
         */
        private Set<String> tlsCiphers;
        /**
         * The SSL protocol used to generate the SSLContext.
         * <p>
         * Default setting is TLS, which is fine for most cases.
         * </p>
         * Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL, SSLv2.
         */
        private Set<String> tlsProtocols;
        /**
         * The number of concurrent lookup requests allowed to send on each broker
         * connection to prevent overload on broker.
         * <p>
         * The default is {@code 5000}.
         */
        private Integer concurrentLookupRequest;
        /**
         * The maximum number of lookup requests allowed on each broker connection to
         * prevent overload on broker.
         * <p>
         * The default is {@code 50000}.
         */
        private Integer maxLookupRequest;
        /**
         * The maximum number of times a lookup-request to a broker will be redirected.
         */
        private Integer maxLookupRedirects;
        /**
         * The maximum number of rejected requests of a broker in a certain time frame
         * (30 seconds) after the current connection is closed and the client creates a
         * new connection to connect to a different broker.
         * <p>
         * The default is {@code 50}.
         */
        private Integer maxNumberOfRejectedRequestPerConnection;
        /**
         * Duration of keeping alive interval for each client broker connection.
         * <p>
         * The default is {@code 30s}.
         */
        private Duration keepAliveInterval;
        /**
         * Duration of waiting for a connection to a broker to be established.
         * <p>
         * If the duration passes without a response from a broker, the connection
         * attempt is dropped.
         * <p>
         * The default is {@code 10s}.
         */
        private Duration connectionTimeout;
        /**
         * Default duration for a backoff interval.
         * <p>
         * The default is {@code 100ns}.
         */
        private Duration defaultBackoffIntervalNanos;
        /**
         * Maximum duration for a backoff interval.
         * <p>
         * The default is {@code 30s}.
         */
        private Duration maxBackoffIntervalNanos;

    }

}
