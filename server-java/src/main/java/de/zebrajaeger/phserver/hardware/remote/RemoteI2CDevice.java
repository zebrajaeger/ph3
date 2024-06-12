package de.zebrajaeger.phserver.hardware.remote;

import de.zebrajaeger.phserver.hardware.i2c.I2CDevice;
import de.zebrajaeger.phserver.util.HexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class RemoteI2CDevice implements I2CDevice {
    private final String host;
    private final int address;
    private final int readTimeout = 2000;
    private final int connectionTimeout = 2000;

    public RemoteI2CDevice(String host, int address) {
        this.host = host;
        this.address = address;
    }

    @Override
    public byte[] read(int count) throws IOException {
        Assert.state(address > 0, "address must be greater than zero");
        Assert.state(address < 128, "address must be less or equal 127");

        String request = String.format("http://%s/read?address=%d&count=%d", host, address, count);
        log.trace("Request '{}'", request);
//        URL url = new URL(request);
        String response = sendRequest(request);
//        String response = IOUtils.toString(url, StandardCharsets.UTF_8);
        // TODO catch java.net.ConnectException and Restart connection
        return HexUtils.decodeHexString(response);
    }

    @Override
    public void write(byte[] data) throws IOException {
        Assert.state(address > 0, "address must be greater than zero");
        Assert.state(address < 128, "address must be less or equal 127");

        String hex = HexUtils.encodeHexString(data);
        String request = String.format("http://%s/write?address=%d&data=%s", host, address, hex);
//        URL url = new URL(request);
        // TODO catch java.net.ConnectException and Restart connection
        sendRequest(request);
//        String ignore = IOUtils.toString(url, StandardCharsets.UTF_8);
    }

    private String sendRequest(String request) throws IOException {
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            int status = connection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return content.toString();
            } else {
                throw new IOException("Request failed with " + status);
            }
        } finally {
            connection.disconnect();
        }
    }
}
