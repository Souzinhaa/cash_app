package com.fho.piggycash.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.util.MaskEditUtil;

import java.util.List;
import java.util.Map;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TransacaoAdapter extends
        RecyclerView.Adapter<TransacaoAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView text_nome_transacao;
        public TextView text_valor_transacao;
        public TextView text_data_transacao;
        public TextView text_titulo_transacao;
        public ImageView img_transacao;
        public View view;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            text_nome_transacao = (TextView) itemView.findViewById(R.id.text_nome_transacao);
            text_valor_transacao = (TextView) itemView.findViewById(R.id.text_valor_transacao);
            text_data_transacao = (TextView) itemView.findViewById(R.id.text_data_transacao);
            text_titulo_transacao = (TextView) itemView.findViewById(R.id.text_titulo_transacao);
            img_transacao = (ImageView) itemView.findViewById(R.id.img_transacao);

            view = itemView;
        }
    }

    private List<TransactionModel> mTransacoes;

    // Pass in the contact array into the constructor
    public TransacaoAdapter(List<TransactionModel> transacoes) {
        mTransacoes = transacoes;
    }

    @Override
    public TransacaoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_transacao, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TransacaoAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        TransactionModel transacao = mTransacoes.get(position);
        //Map<String, Object> map = (Map<String, Object>) mTransacoes.get(position);
        //TransactionModel transacao = new TransactionModel(map.get("nome").toString(), Double.parseDouble(map.get("valor").toString()));
        //transacao.setData(map.get("data").toString());
        //transacao.setDeposito(Integer.valueOf(map.get("deposito").toString()));

        // Set item views based on your views and data model
        TextView textView = holder.text_nome_transacao;
        textView.setText(transacao.getNome());

        TextView textViewNome = holder.text_data_transacao;
        textViewNome.setText(transacao.getData());

        ImageView imageView = holder.img_transacao;

        if(!transacao.valueIsDeposito()) {
            TextView textViewTitulo = holder.text_titulo_transacao;
            textViewTitulo.setText(holder.view.getContext().getString(R.string.retirada_confirmada));
            Drawable icon_retirar = holder.view.getContext().getDrawable(R.drawable.icon_retirar_cash);
            imageView.setBackground(icon_retirar);
        }
        else {
            Drawable icon_depositar = holder.view.getContext().getDrawable(R.drawable.icon_depositar);
            imageView.setBackground(icon_depositar);
        }
        TextView textView2 = holder.text_valor_transacao;
        textView2.setText(MaskEditUtil.addValueMask(transacao.getValor()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTransacoes.size();
    }
}