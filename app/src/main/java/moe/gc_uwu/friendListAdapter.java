package moe.gc_uwu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class friendListAdapter extends ArrayAdapter<friendTemplate> implements View.OnClickListener {
    private ArrayList<friendTemplate> dataSet;
    Context mContext;

    @Override
    public void onClick(View v) {

    }

    // View lookup cache
    private static class ViewHolder {
        TextView id;
        TextView title;
    }

    public friendListAdapter(ArrayList<friendTemplate> data, Context context) {
        super(context, R.layout.music_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        musicTemplate dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final friendListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new friendListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.music_item, parent, false);
            viewHolder.id = (TextView) convertView.findViewById(R.id.ID);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            //result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (friendListAdapter.ViewHolder) convertView.getTag();
            //result=convertView;
        }

        viewHolder.id.setText(dataModel.getId());
        viewHolder.title.setText(dataModel.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
