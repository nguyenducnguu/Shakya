package dn.ute.shakya.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.R;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.Lesson;
import dn.ute.shakya.models.Word;

public class ObjectWordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Word> lstData;
    Context mContext = null;
    OnAdapterListening mListening = null;

    public ObjectWordAdapter(List<Word> lstData, OnAdapterListening mListening){
        this.lstData = lstData;
        this.mListening = mListening;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_word, parent, false);
        viewHolder = new ObjectWordAdapter.WordViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ObjectWordAdapter.WordViewHolder vh = (ObjectWordAdapter.WordViewHolder) holder;
        Word word = lstData.get(position);

        vh.tv_wordId.setText(word.getId() + "");
        vh.tv_word.setText((position + 1) + ". " + word.getContent());

        vh.ln_wordItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                PopupMenu popup = new PopupMenu(mContext, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        long wordId = getWordId(view);
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editWord(wordId);
                                break;
                            case R.id.delete:
                                removeWord(wordId);
                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                return false;
            }
        });
    }

    private void editWord(final long wordId){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_word);

        final EditText edt_word = dialog.findViewById(R.id.edt_word);

        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        final TableWord tableWord = new TableWord(mContext);
        edt_word.setText(tableWord.getWord(wordId).getContent());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListening != null) mListening.onReloadUI(false);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_word.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "Please enter content!!!", Toast.LENGTH_SHORT).show();
                    edt_word.setText("");
                    edt_word.requestFocus();
                    return;
                }
                Word word = tableWord.getWord(wordId);
                word.setContent(edt_word.getText().toString().trim());
                tableWord.updateWord(word);
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                for (Word w: lstData) {
                    if(w.getId() == wordId){
                        w.setContent(word.getContent());
                        break;
                    }
                }
                if(mListening != null) mListening.onReloadUI(false);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private boolean removeWord(long wordId){
        TableWord tableWord = new TableWord(mContext);
        Word word = tableWord.getWord(wordId);
        if(word == null) return false;

        tableWord.deleteWord(word);
        for (Word w: lstData) {
            if(w.getId() == wordId) {
                lstData.remove(w);
                break;
            }
        }
        if(mListening != null) mListening.onReloadUI(true);
        notifyDataSetChanged();
        return true;
    }

    private long getWordId(View view){
        TextView tv_wordId = view.findViewById(R.id.tv_wordId);
        long wordId = -1;
        try {
            wordId = Integer.parseInt(tv_wordId.getText().toString());
        }
        catch (Exception e){
            wordId = -1;
        }
        return wordId;
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_wordItem;
        TextView tv_wordId, tv_word;

        public WordViewHolder(final View itemView) {
            super(itemView);
            tv_wordId = itemView.findViewById(R.id.tv_wordId);
            tv_word = itemView.findViewById(R.id.tv_word);
            ln_wordItem = itemView.findViewById(R.id.ln_wordItem);
        }
    }
}
