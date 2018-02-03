package com.parse.starter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parse.ParseObject;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> postagens;

    public HomeAdapter(@NonNull Context c, @NonNull ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.postagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        /*
        * Verifica se não existe o objeto view criado, pois a view utilizada é armazenada
        * no cache do android e fica na variável convertView
        * */
        if(view == null){
            //Inicializa o objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_postagem, parent, false);
        }

        //Verifica se existem postagens
        if(postagens.size() > 0){
            //Recupera componentes da tela
            ImageView imagemPostagem = (ImageView) view.findViewById(R.id.image_lista_postagem);

            ParseObject parseObject = postagens.get(position);
            parseObject.getParseFile("imagem"); //se quisesse pegar um campo normal, basta usar o método get e passar o nome do campo
            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .into(imagemPostagem);

        }

        return view;

    }
}
