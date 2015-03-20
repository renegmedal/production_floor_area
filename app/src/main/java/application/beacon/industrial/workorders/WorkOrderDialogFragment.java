package application.beacon.industrial.workorders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import application.beacon.industrial.R;
import application.beacon.industrial.service.WorkOrder;
import application.beacon.industrial.util.RxBus;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by rene on 2/11/15.
 */
public class WorkOrderDialogFragment extends DialogFragment {

    private View mPromptsView;
    private static String mUuid;
    private static List<WorkOrder> mWorkOrderList;
    private ListView itemListView;

    public static WorkOrderDialogFragment newInstance(String uuid, List<WorkOrder> workOrderList) {
        mUuid = uuid;
        mWorkOrderList = workOrderList;
        return new WorkOrderDialogFragment();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        mPromptsView = li.inflate(R.layout.workorder_dialog_prompt, null);
        itemListView = (ListView) mPromptsView.findViewById(R.id.item);
        return mPromptsView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] itemArray = new String[mWorkOrderList.size()];
        int i = 0;
        for (WorkOrder workOrder: mWorkOrderList) {
            itemArray[i++] = workOrder.getOrderId() + "  " +
                    workOrder.getDescription() + "  " +
                    workOrder.getAssignTo() + "  " +
                    workOrder.getStatus();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, itemArray);

        itemListView.setAdapter(adapter);
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        //LayoutInflater li = LayoutInflater.from(getActivity());
//        //mPromptsView = li.inflate(R.layout.workorder_dialog_prompt, null);
//
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(mPromptsView);
//
//        builder.setPositiveButton(R.string.button_done,
//
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        return;
//                    }
//                });
//
//        return builder.create();
//    }

}
