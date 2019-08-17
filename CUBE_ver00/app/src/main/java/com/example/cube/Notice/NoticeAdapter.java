package com.example.cube.Notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.NoticeData;
import com.example.cube.R;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<NoticeData> NoticeList;
    private Context mContext;
    public NoticeAdapter(ArrayList<NoticeData> NoticeList) {
        this.NoticeList = NoticeList;
    }
    @NotNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new ItemViewHolder(view);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mDateText;
        private TextView mNewTagText;
        private TextView mClickNumText;
        private TextView mCommentNumText;

       public ItemViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.board_item_title);
            mDateText = itemView. findViewById(R.id.board_item_date);
            mNewTagText = itemView.findViewById(R.id.board_item_newtag);
            mCommentNumText = itemView.findViewById(R.id.board_item_num_comments);
            mClickNumText = itemView. findViewById(R.id.board_item_numclick);
        }

    }

    @Override
    public void onBindViewHolder(@NotNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        NoticeData data = NoticeList.get(position);
        holder.mTitleText.setText(data.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
        holder.mDateText.setText(simpleDateFormat.format(data.getDate()));
        if ((new Date().getTime() - data.getDate().getTime()) / (24 * 60 * 60 * 1000) >= 1)
            holder.mNewTagText.setVisibility(View.GONE);
        holder.mCommentNumText.setText(Integer.toString(data.getNumComments()));
        holder.mClickNumText.setText(Integer.toString(data.getNumClicks()));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return NoticeList.size();
    }

    void addItem(NoticeData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        NoticeList.add(data);
    }


}