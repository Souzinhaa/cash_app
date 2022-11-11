package com.fho.piggycash.screen.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.AppService;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;

import java.util.List;

public class TransactionAdapter extends
        RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_name_transaction;
        public TextView text_value_transaction;
        public TextView text_date_transaction;
        public TextView text_tittle_transaction;
        public ImageView img_transaction;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);

            text_name_transaction = (TextView) itemView.findViewById(R.id.text_name_transaction);
            text_value_transaction = (TextView) itemView.findViewById(R.id.text_value_transaction);
            text_date_transaction = (TextView) itemView.findViewById(R.id.text_date_transaction);
            text_tittle_transaction = (TextView) itemView.findViewById(R.id.text_tittle_transaction);
            img_transaction = (ImageView) itemView.findViewById(R.id.img_transaction);

            view = itemView;
        }
    }

    private final List<TransactionModel> mTransactions;

    public TransactionAdapter(List<TransactionModel> transacoes) {
        mTransactions = transacoes;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_transaction, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, int position) {
        TransactionModel transaction = mTransactions.get(position);

        View v = holder.view;
        v.setOnTouchListener(new OnSwipeTouchListener(v.getContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                AppService.getInstance().deleteTransaction(v, holder.getAdapterPosition());
            }
        });

        TextView textView = holder.text_name_transaction;
        textView.setText(transaction.getName());

        TextView textViewNome = holder.text_date_transaction;
        textViewNome.setText(transaction.getDate());

        ImageView imageView = holder.img_transaction;

        if(!transaction.valueIsDeposit()) {
            TextView textViewTitulo = holder.text_tittle_transaction;
            textViewTitulo.setText(holder.view.getContext().getString(R.string.retirada_confirmada));
            Drawable icon_retirar = AppCompatResources.getDrawable(holder.view.getContext(), R.drawable.icon_retirar_cash);
            imageView.setBackground(icon_retirar);
        }
        else {
            Drawable icon_depositar = AppCompatResources.getDrawable(holder.view.getContext(), R.drawable.icon_depositar);
            imageView.setBackground(icon_depositar);
        }
        TextView textView2 = holder.text_value_transaction;
        textView2.setText(MaskEditUtil.addValueMask(transaction.getValue()));
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}