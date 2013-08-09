package org.gopivotal.app.http.converter.json;

import java.io.IOException;
import java.nio.charset.Charset;

import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

/**
 * The JsonToPdxInstanceHttpMessageConverter class is an HttpMessageConverter implementation that converts between JSON
 * and PdxInstances.
 * <p/>
 * @author John Blum
 * @see com.gemstone.gemfire.pdx.JSONFormatter
 * @see com.gemstone.gemfire.pdx.PdxInstance
 * @see org.springframework.http.converter.AbstractHttpMessageConverter
 * @since 7.5
 */
@SuppressWarnings("unused")
public class JsonToPdxInstanceHttpMessageConverter extends AbstractHttpMessageConverter<PdxInstance> {

  protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public JsonToPdxInstanceHttpMessageConverter() {
    super(new MediaType("application", "json", DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET));
  }

  @Override
  protected PdxInstance readInternal(final Class<? extends PdxInstance> type, final HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException
  {
    final byte[] jsonBytes = FileCopyUtils.copyToByteArray(inputMessage.getBody());
    return JSONFormatter.fromJSON(jsonBytes);
  }

  protected void setContentLength(final HttpOutputMessage message, final byte[] content) {
    message.getHeaders().setContentLength(content.length);
  }

  @Override
  protected boolean supports(final Class<?> type) {
    return (type != null && PdxInstance.class.isAssignableFrom(type));
  }

  @Override
  protected void writeInternal(final PdxInstance pdxInstance, final HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException
  {
    final String json = JSONFormatter.toJSON(pdxInstance);

    final byte[] jsonBytes = json.getBytes();

    outputMessage.getBody().write(jsonBytes, 0, jsonBytes.length);
    outputMessage.getBody().flush();
    setContentLength(outputMessage, jsonBytes);
  }

}
