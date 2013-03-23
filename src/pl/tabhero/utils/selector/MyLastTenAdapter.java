package pl.tabhero.utils.selector;

import java.util.List;
import pl.tabhero.R;
import pl.tabhero.core.ItemOfLastTen;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyLastTenAdapter extends BaseAdapter implements OnClickListener {
    
    private Context context;
    private List<ItemOfLastTen> listOfLastTen; 
    
    public MyLastTenAdapter(Context context, List<ItemOfLastTen> listOfLastTen) {
        this.context = context;
        this.listOfLastTen = listOfLastTen;
    }

    @Override
    public int getCount() {
        return listOfLastTen.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfLastTen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemOfLastTen entry = listOfLastTen.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.boxoflasttenlistview, null);
        }
        ImageView favImage = (ImageView) convertView.findViewById(R.id.favImage);
        ImageView netImage = (ImageView) convertView.findViewById(R.id.netImage);
        favImage.setVisibility(View.INVISIBLE);
        netImage.setVisibility(View.INVISIBLE);
        TextView tvPerf = (TextView) convertView.findViewById(R.id.perf);
        tvPerf.setText(entry.getPerformer());
        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        tvTitle.setText(entry.getTitle() + " ");
        if (entry.getType().equals(context.getString(R.string.fav))) {
            favImage.setVisibility(View.VISIBLE);
        } else if (entry.getType().equals(context.getString(R.string.net))) {
            netImage.setVisibility(View.VISIBLE);
        }
        /*TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        tvDate.setText(entry.getDate());
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        tvType.setText(entry.getType());*/
        return convertView;
    }

    @Override
    public void onClick(View v) {
        //ItemOfLastTen entry = (ItemOfLastTen) v.getTag();
        notifyDataSetChanged();
    }
}
