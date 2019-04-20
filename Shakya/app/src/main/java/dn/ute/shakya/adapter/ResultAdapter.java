package dn.ute.shakya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dn.ute.shakya.R;
import dn.ute.shakya.common.Const;

public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<String> lstData;
    ArrayList<String> lstResult;
    Context mContext = null;

    public ResultAdapter(ArrayList<String> lstData, ArrayList<String> lstResult){
        this.lstData = lstData;
        this.lstResult = lstResult;
        for (int i = 0; i < lstResult.size(); i++){
            Log.d("lstResult_ResultAdapter", "lstData = " + this.lstData.size() + " | lstResult[" + i + "] = " + this.lstResult.get(i));
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_result, parent, false);
        viewHolder = new ResultViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ResultViewHolder vh = (ResultViewHolder) holder;
        String word = Const.DEFAULTANSWER;
        String answer = Const.DEFAULTANSWER;
        try {
            word = lstData.get(position);
            answer = lstResult.get(position);
        }
        catch (Exception e){

        }

        vh.tv_word.setText(word);
        vh.tv_answer.setText(answer);

        word = word.trim().toUpperCase();
        answer = answer.trim().toUpperCase();
        if(word.equals(answer) || removeAllNonWordCharacters(word).equals(removeAllNonWordCharacters(answer))){
            vh.tv_answer.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        else {
            vh.tv_answer.setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_word, tv_answer;

        public ResultViewHolder(final View itemView) {
            super(itemView);
            tv_word = itemView.findViewById(R.id.tv_word);
            tv_answer = itemView.findViewById(R.id.tv_answer);
        }
    }

    private String removeAllNonWordCharacters(String str){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(str.toLowerCase());

        for(int i = 0; i < sb.length(); i++){
            if(alphabet.indexOf(sb.charAt(i)) == -1){
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString();
    }
}
