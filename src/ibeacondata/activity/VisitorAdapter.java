package ibeacondata.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.accelerometersensortest.R;
import ibeacondata.bean.BeaconBean;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LK on 2016/10/30.
 */
public class VisitorAdapter extends BaseAdapter {
    private List<BeaconBean> visitorList;
    private LayoutInflater layoutInflater = null;
    private ViewHolder holder = null;
    Context context;
    private ReentrantLock dataLock = null;


    public VisitorAdapter(Context context,List<BeaconBean> visitorList){
        this.visitorList = visitorList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        dataLock = new ReentrantLock();
    }

    public void updateData(Collection<BeaconBean> data) {
        dataLock.lock();
        visitorList.clear();
        visitorList.addAll(data);
        dataLock.unlock();
    }
    public void setData(Collection<BeaconBean> data){
        visitorList.clear();
        visitorList.addAll(data);
    }
    @Override
    public int getCount() {
        return visitorList.size();
    }

    @Override
    public Object getItem(int i) {
        return visitorList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.visitoradapter,viewGroup,false);
            holder = new ViewHolder();
            holder.macTextView = (TextView) view.findViewById(R.id.mac);
            holder.majorTextView = (TextView) view.findViewById(R.id.major);
            holder.minorTextView = (TextView) view.findViewById(R.id.minor);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        if (visitorList.size()>0){
            holder.macTextView.setText(visitorList.get(i).getMac_id());
            holder.majorTextView.setText(String.valueOf(visitorList.get(i).getMajor()));
            holder.minorTextView.setText(String.valueOf(visitorList.get(i).getMinor()));
        }
        return view;
    }

    class ViewHolder{
        private TextView macTextView = null;
        private TextView majorTextView = null;
        private TextView minorTextView = null;
    }
}
