package moe.gc_uwu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.URLDecoder;
import java.util.ArrayList;

public class rankingAdapter extends ArrayAdapter<dataTemplate> implements View.OnClickListener{

    private ArrayList<dataTemplate> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView score;
        TextView title;
        TextView site;
    }

    public rankingAdapter(ArrayList<dataTemplate> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        /*int position=(Integer) v.getTag();
        Object object= getItem(position);
        dataTemplate dataModel=(dataTemplate)object;

        switch (v.getId())
        {
            case R.id.score:
                Snackbar.make(v, dataTemplate.getScore(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    //private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String tmp;
        // Get the data item for this position
        dataTemplate dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.score = (TextView) convertView.findViewById(R.id.score_val);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.site = (TextView) convertView.findViewById(R.id.site);
            //result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        //lastPosition = position;

        tmp = URLDecoder.decode(dataModel.getPlayer());
        viewHolder.name.setText(tmp);
        viewHolder.score.setText(dataModel.getScore());
        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.site.setText(dataModel.getSite());
        // Return the completed view to render on screen
        return convertView;
    }
}
