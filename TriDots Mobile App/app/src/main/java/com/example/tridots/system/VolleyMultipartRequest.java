package com.example.tridots.system;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class VolleyMultipartRequest extends Request<String> {
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;
    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;

    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY = "*****";

    public VolleyMultipartRequest(int method, String url, Map<String, String> params, Map<String, DataPart> byteData,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mParams = params;
        mByteData = byteData;
        mListener = listener;
        mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            // Add regular parameters
            if (mParams != null) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                    outputStream.writeBytes(LINE_END);
                    outputStream.writeBytes(entry.getValue());
                    outputStream.writeBytes(LINE_END);
                }
            }

            // Add byte data
            if (mByteData != null) {
                for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                    DataPart dataPart = entry.getValue();
                    outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + dataPart.getFileName() + "\"" + LINE_END);
                    outputStream.writeBytes("Content-Type: " + dataPart.getType() + LINE_END);
                    outputStream.writeBytes(LINE_END);
                    outputStream.write(dataPart.getData());
                    outputStream.writeBytes(LINE_END);
                }
            }

            // End of multipart form data.
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.getBody();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    @Override
    protected Response<String> parseNetworkResponse(com.android.volley.NetworkResponse response) {
        String parsed;
        if (response.statusCode == 200) {
            parsed = new String(response.data);
            return Response.success(parsed, getCacheEntry());
        } else {
            parsed = new String(response.data);
            return Response.error(new com.android.volley.ParseError(new Exception(parsed)));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    public static class DataPart {
        private String fileName;
        private byte[] data;
        private String type;

        public DataPart(String fileName, byte[] data) {
            this.fileName = fileName;
            this.data = data;
            this.type = "image/jpeg"; // Set default type or set dynamically
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return data;
        }

        public String getType() {
            return type;
        }
    }
}
