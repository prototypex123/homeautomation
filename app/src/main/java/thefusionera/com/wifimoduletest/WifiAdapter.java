package thefusionera.com.wifimoduletest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;


public class WifiAdapter extends RecyclerView.Adapter<WifiViewHolder> {


    private static final String TAG = ":::::::::::::::::::";
    private Activity activity;
    private List<ScanResult> scanResults;
    private String ipAddress;
    private Intent intent;
    private String ssid;
    private String password;
    ProgressDialog pd;


    public WifiAdapter(Activity activity, List<ScanResult> scanResults, String ipAddress) {
        this.activity = activity;
        this.scanResults = scanResults;
        this.ipAddress = ipAddress;
        pd = new ProgressDialog(activity);
        pd.setMessage("Fetching IP Address");
        pd.setTitle("Please Wait...");

        intent = new Intent(activity, MainActivity.class);

    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.row, parent, false);

        return new WifiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WifiViewHolder holder, int position) {
        holder.ssidName.setText(scanResults.get(holder.getAdapterPosition()).SSID);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssid = holder.ssidName.getText().toString();
                password = "12345678";
                intent.putExtra("ssid", ssid);
                intent.putExtra("password", password);
                    //get capabilities of current connection
                    String Capabilities =  scanResults.get(holder.getAdapterPosition()).capabilities;
                    Log.d (TAG, scanResults.get(holder.getAdapterPosition()).SSID + " capabilities : " + Capabilities);

                    if (Capabilities.contains("WPA2")) {
                        intent.putExtra("encryptionType", "WPA2");
                    }
                    else if (Capabilities.contains("WPA")) {
                        intent.putExtra("encryptionType", "WPA");
                    }
                    else if (Capabilities.contains("WEP")) {
                        intent.putExtra("encryptionType", "WEP");
                    }

                new UdpSendTask().execute();

                    }
//                }).start();
//            }
        });
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }


    private class UdpSendTask extends AsyncTask<Void, Void, String> {

        DatagramSocket socket = null;
        DatagramPacket sendPacket, receivePacket;


        public UdpSendTask() {

            pd.setCancelable(true);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // actually could set running = false; right here, but I'll
                    // stick to contract.
                    cancel(true);
                }
            });

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String msg = ssid + "/" + password;
            int port = 2390;
            byte receiveBuff[] = new byte[1024];
            String data = null;
            byte[] buff = (msg.getBytes());
            try {
                socket = new DatagramSocket(port);
                socket.setBroadcast(true);
                Log.d(TAG, "sendData: Socket Created");
//                InetAddress ip = InetAddress.getByName("192.168.1.7");
                InetAddress ip = InetAddress.getByName(ipAddress);
                sendPacket = new DatagramPacket(buff, buff.length, ip, port);
                Log.d(TAG, "sendData: Send Packet Created");
                socket.send(sendPacket);
                Log.d(TAG, "sendData: Packet Sent");

                receivePacket = new DatagramPacket(receiveBuff, receiveBuff.length);
                Log.d(TAG, "doInBackground: Receive Packet Created");
                Log.d(TAG, "doInBackground: Waiting to receive....");
                socket.receive(receivePacket);
                Log.d(TAG, "doInBackground: Packet received");
                data = new String(receivePacket.getData(), 0, receivePacket.getLength());

                Log.d(TAG, "doInBackground: Data == " + data);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                    Log.d(TAG, "sendData: Socket Closed");
                }
            }
            return data;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            intent.putExtra("ip_address", s);
            pd.dismiss();
            activity.startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "onCancelled: Cancelled");
            pd.dismiss();
        }
    }

}
