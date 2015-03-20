package application.beacon.industrial.workorders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import application.beacon.industrial.R;
import application.beacon.industrial.service.WorkOrder;

/**
 *
 */
public class WorkOrderListAdapter extends RecyclerView.Adapter<WorkOrderListAdapter.ViewHolder> {


    private WorkOrder[] mDataSet;

    /**
     * Custom viewholder for work orders.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mJobOrderTextView;
        private final TextView mDescriptionTextView;
        private final TextView mAssignedToTextView;
        private final TextView mStatusTextView;

        public ViewHolder(View v) {
            super(v);
            mJobOrderTextView = (TextView) v.findViewById(R.id.jobOrderTextView);
            mDescriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);
            mAssignedToTextView = (TextView) v.findViewById(R.id.assignedToTextView);
            mStatusTextView = (TextView) v.findViewById(R.id.statusTextView);

        }

        public TextView getJobOrderTextView() { return mJobOrderTextView; }
        public TextView getDescriptionTextView() { return mDescriptionTextView;}
        public TextView getAssignedToTextView() { return mAssignedToTextView;}
        public TextView getStatusTextView() { return mStatusTextView; }

    }

    public WorkOrderListAdapter(WorkOrder[] dataSet) {
        mDataSet = dataSet;
    }

    // ---- implement methods ---------

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workorder_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mJobOrderTextView.setText(mDataSet[position].getOrderId());
        viewHolder.mDescriptionTextView.setText(mDataSet[position].getDescription());
        viewHolder.mAssignedToTextView.setText(mDataSet[position].getAssignTo());
        viewHolder.mStatusTextView.setText(mDataSet[position].getStatus());
    }

    @Override
    public int getItemCount() {
        if (mDataSet != null)
            return mDataSet.length;
        else
            return 0;
    }
}
