package pl.tabhero.utils.selector;

import java.util.List;
import pl.tabhero.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectArralAdapter extends ArrayAdapter<ItemsOnCheckboxList> {
    private LayoutInflater inflater;

    public SelectArralAdapter(Context context, List<ItemsOnCheckboxList> itemsList) {
        super(context, R.layout.listwithcheckbox, R.id.rowDelArtistsView, itemsList);
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Planet to display
        ItemsOnCheckboxList item = (ItemsOnCheckboxList) this.getItem(position);
        // The child views in each row.
        CheckBox checkBox;
        TextView textView;
        // Create a new row view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listwithcheckbox, null);
            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.rowDelArtistsView);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            // Optimization: Tag the row with it's child views, so we don't
            // have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new SelectViewHolder(textView, checkBox));
            // If CheckBox is toggled, update the planet it is tagged with.
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ItemsOnCheckboxList item = (ItemsOnCheckboxList) cb.getTag();
                    item.setChecked(cb.isChecked());
                }
            });
        } else {
            // Because we use a ViewHolder, we avoid having to call
            // findViewById().
            SelectViewHolder viewHolder = (SelectViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }
        // Tag the CheckBox with the Planet it is displaying, so that we can
        // access the planet in onClick() when the CheckBox is toggled.
        checkBox.setTag(item);
        // Display planet data
        checkBox.setChecked(item.isChecked());
        textView.setText(item.getName());
        return convertView;
    }
}
