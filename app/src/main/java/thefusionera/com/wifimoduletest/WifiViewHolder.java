package thefusionera.com.wifimoduletest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Neeraj Athalye on 06-Sep-17.
 */

public class WifiViewHolder extends RecyclerView.ViewHolder {

    TextView ssidName;
    LinearLayout layout;

    public WifiViewHolder(View itemView) {
        super(itemView);

        ssidName = itemView.findViewById(R.id.ssid_name);
        layout = (LinearLayout) itemView.findViewById(R.id.layout);

    }
}
