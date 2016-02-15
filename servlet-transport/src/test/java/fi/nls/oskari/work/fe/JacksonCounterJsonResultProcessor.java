package fi.nls.oskari.work.fe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fi.nls.oskari.utils.GeometryJSONOutputModule;

public class JacksonCounterJsonResultProcessor extends CounterJsonResultProcessor {

    protected ObjectMapper json = new ObjectMapper();
    protected ObjectWriter writer;

    public JacksonCounterJsonResultProcessor() {
        json.setSerializationInclusion(Include.NON_NULL);
        writer = json.writer(new DefaultPrettyPrinter());

        GeometryJSONOutputModule simpleModule = new GeometryJSONOutputModule();

        json.registerModule(simpleModule);

    }

    @Override
    public void addResults(String clientId, String channel, Object data) {
        // display a snapshot
        if (resultsCounter < 10) {

            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            try {
                writer.writeValue(outs, data);
            } catch (JsonGenerationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JsonMappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            log.debug(new String(outs.toByteArray()));
        }
        logResults(channel);
    }

}
