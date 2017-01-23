    package com.springml.marketo.rest.client;

    import com.springml.marketo.rest.client.impl.LeadDatabaseClientImpl;
    import com.springml.marketo.rest.client.util.HttpHelper;
    import org.apache.commons.io.IOUtils;
    import org.apache.commons.lang3.reflect.FieldUtils;
    import org.junit.Before;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;

    import java.lang.reflect.Field;
    import java.util.HashMap;
    import java.util.Map;

    import static com.springml.marketo.rest.client.util.MarketoClientConstants.*;
    import static org.mockito.Mockito.reset;
    import static org.mockito.Mockito.when;

    /**
     * Junit for BaseDatabaseClient
     */
    public abstract class BaseDatabaseClientTest {

        protected static String CLIENT_ID = "a36c5936-79e4-4e22-9995-1c8088033663";
        protected static String CLIENT_SECRET = "4kDFleFLinsCZWa6PEQ1JDt5AcmTGBPa";
        protected static String MARKETO_BASEURI = "https://559-AGH-810.mktorest.com";
        protected static  String PAGING_TOKEN = "BR5X7EWI5FGQ42EOSFUGFBMYCF36D5CCQVSNLURZFMOIGN7X4MHA====";

        protected LeadDatabaseClient leadDatabaseClient;

        private String authResponse;

        @Mock
        protected HttpHelper httpHelper;

        @Before
        public void setUp() throws Exception {
            MockitoAnnotations.initMocks(this);

            reset(httpHelper);

            leadDatabaseClient = MarketoClientFactory.getLeadDatabaseClient(CLIENT_ID, CLIENT_SECRET, MARKETO_BASEURI);

            Field field = FieldUtils.getField(LeadDatabaseClientImpl.class, "httpHelper", true);
            field.set(leadDatabaseClient, httpHelper);

            Field fieldId = FieldUtils.getField(LeadDatabaseClientImpl.class, "sessionId", true);
            fieldId.set(leadDatabaseClient, "e4ceb18c-f578-4ecb:sj");

            Map<String, String> authParams = new HashMap<>(8);
            authParams.put(STR_GRANT_TYPE, STR_CLIENT_CREDENTIALS);
            authParams.put(STR_CLIENT_ID, CLIENT_ID);
            authParams.put(STR_CLIENT_SECRET, CLIENT_SECRET);

            ClassLoader classLoader = getClass().getClassLoader();
            authResponse = IOUtils.toString(classLoader.getResourceAsStream("authResponse.json"), "UTF-8");

            when(httpHelper.get(MARKETO_BASEURI, STR_OAUTH_PATH, authParams)).thenReturn(authResponse);

        }
    }
