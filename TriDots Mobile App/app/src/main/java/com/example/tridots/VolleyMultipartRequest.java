package com.example.tridots;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * An abstract Volley request class for making multipart requests (file uploads).
 * Subclasses must implement getParams() and getByteData() to provide the text and file parts.
 */
public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final String BOUNDARY = "apiclient-" + System.currentTimeMillis();
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // text params
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(bos, params, getParamsEncoding());
            }
            // data params
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                dataParse(bos, data);
            }
            bos.write(("--" + BOUNDARY + "--\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    /**
     * The method that returns the map of data parts for the request.
     * The key is the name of the parameter, and the value is the DataPart object.
     * @return A map of data parts.
     */
    protected abstract Map<String, DataPart> getByteData() throws AuthFailureError;

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new VolleyError(response));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    private void textParse(ByteArrayOutputStream bos, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                bos.write(("--" + BOUNDARY + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                bos.write((entry.getValue() + "\r\n").getBytes(encoding));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + encoding, e);
        }
    }

    private void dataParse(ByteArrayOutputStream bos, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            bos.write(("--" + BOUNDARY + "\r\n").getBytes());
            bos.write(("Content-Disposition: form-data; name=\"" +
                    entry.getKey() + "\"; filename=\"" +
                    entry.getValue().getFileName() + "\"\r\n").getBytes());
            bos.write(("Content-Type: " + entry.getValue().getType() + "\r\n\r\n").getBytes());
            bos.write(entry.getValue().getContent());
            bos.write("\r\n".getBytes());
        }
    }

    /**
     * Inner class to represent a data part (file) for the multipart request.
     */
    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        /**
         * @param name The name of the file.
         * @param data The byte array of the file's content.
         * @param mimeType The MIME type of the file.
         */
        public DataPart(String name, byte[] data, String mimeType) {
            fileName = name;
            content = data;
            this.type = mimeType;
        }

        String getFileName() {
            return fileName;
        }

        byte[] getContent() {
            return content;
        }

        String getType() {
            return type;
        }
    }
}
