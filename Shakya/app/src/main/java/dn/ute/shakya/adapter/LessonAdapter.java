package dn.ute.shakya.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import dn.ute.shakya.ViewLessonActivity;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.Lesson;

public class LessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Lesson> lstData;
    Context mContext = null;
    OnAdapterListening mListening = null;

    public LessonAdapter(List<Lesson> lstData, OnAdapterListening mListening){
        this.lstData = lstData;
        this.mListening = mListening;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_lesson, parent, false);
        viewHolder = new LessonAdapter.LessonViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final LessonAdapter.LessonViewHolder vh = (LessonAdapter.LessonViewHolder) holder;
        final Lesson lesson = lstData.get(position);

        vh.tv_lessonId.setText(lesson.getId() + "");
        vh.tv_lesson.setText(lesson.getTitle());
        TableWord tableWord = new TableWord(mContext);
        int countWord = tableWord.getWordsCountWithLessonId(lesson.getId());
        vh.tv_wordCount.setText(countWord + " word");

        vh.ln_lessonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long lessonId = getLessonId(view);
                Intent intent = new Intent(mContext, ViewLessonActivity.class);
                intent.putExtra("lessonId", lessonId);
                mContext.startActivity(intent);
            }
        });
        vh.ln_lessonItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                PopupMenu popup = new PopupMenu(mContext, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        long lessonId = getLessonId(view);
                        if(lessonId == -1) return true;
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editLesstion(lessonId);
                                break;
                            case R.id.delete:
                                removeLesson(lessonId);
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

    private void editLesstion(final long lessonId){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_lesson);

        final EditText edt_title = dialog.findViewById(R.id.edt_title);

        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        final TableLesson tableLesson = new TableLesson(mContext);
        edt_title.setText(tableLesson.getLesson(lessonId).getTitle());

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
                if (edt_title.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "Please enter title!!!", Toast.LENGTH_SHORT).show();
                    edt_title.setText("");
                    edt_title.requestFocus();
                    return;
                }
                Lesson lesson = tableLesson.getLesson(lessonId);
                lesson.setTitle(edt_title.getText().toString().trim());
                tableLesson.updateLesson(lesson);
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                for (Lesson l: lstData) {
                    if(l.getId() == lessonId){
                        l.setTitle(lesson.getTitle());
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

    private boolean removeLesson(long lessonId){
        TableLesson tableLesson = new TableLesson(mContext);
        TableWord tableWord = new TableWord(mContext);
        Lesson lesson = tableLesson.getLesson(lessonId);
        if(lesson == null) return false;

        tableLesson.deleteLesson(lesson);
        tableWord.deleteWordWithLessonId(lessonId);
        for (Lesson l: lstData) {
            if(l.getId() == lessonId) {
                lstData.remove(l);
                break;
            }
        }
        if(mListening != null) mListening.onReloadUI(true);
        notifyDataSetChanged();
        return true;
    }

    private long getLessonId(View view){
        TextView tv_lessonId = view.findViewById(R.id.tv_lessonId);
        long lessonId = -1;
        try {
            lessonId = Integer.parseInt(tv_lessonId.getText().toString());
        }
        catch (Exception e){
            lessonId = -1;
        }
        return lessonId;
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_lessonItem;
        TextView tv_lessonId, tv_lesson, tv_wordCount;

        public LessonViewHolder(final View itemView) {
            super(itemView);
            ln_lessonItem = itemView.findViewById(R.id.ln_lessonItem);
            tv_lessonId = itemView.findViewById(R.id.tv_lessonId);
            tv_lesson = itemView.findViewById(R.id.tv_lesson);
            tv_wordCount = itemView.findViewById(R.id.tv_wordCount);
        }
    }
}
