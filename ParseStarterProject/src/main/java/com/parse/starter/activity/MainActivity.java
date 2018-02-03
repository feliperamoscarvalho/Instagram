/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.util.SlidingTabLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      //Configurar toolbar
      toolbarPrincipal = (Toolbar) findViewById(R.id.toolbar_principal);
      toolbarPrincipal.setLogo(R.drawable.instagramlogo);
      setSupportActionBar(toolbarPrincipal);

      //Configurar as abas
      slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main);
      viewPager = (ViewPager) findViewById(R.id.view_pager_main);

      //Configurar o adapter
      TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
      viewPager.setAdapter(tabsAdapter);
      slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.text_item_tab);
      slidingTabLayout.setDistributeEvenly(true); //abas preenchem o espaço disponível na tela
      slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.cinzaEscuro));
      slidingTabLayout.setViewPager(viewPager);

  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_sair:
                deslogarUsuario();
                return true;
            case R.id.action_configuracoes:
                return true;
            case R.id.action_compartilhar:
                compartilharFoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void compartilharFoto(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Testar processo de retorno dos dados
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri localImagemSelecionada = data.getData();

            //recupera a imagem do local selecionado
            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                //Comprimir no formato PNG
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.PNG, 75, stream);
                //Cria um array de bytes da imagem
                byte[] byteArray = stream.toByteArray();

                //Cria um arquivo no formato aceito pelo Parse
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                String nomeImagem = dateFormat.format(new Date());
                ParseFile arquivoParse = new ParseFile(nomeImagem + ".png", byteArray);

                //Monta o objeto para salvar no Parse
                ParseObject parseObject = new ParseObject("Imagem");
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                parseObject.put("imagem", arquivoParse);

                //Salvar os dados
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "Imagem adicionada com sucesso!", Toast.LENGTH_LONG).show();

                            //Atualiza a listagem de itens do Fragment Home
                            TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
                            HomeFragment homeFragmentNovo = (HomeFragment) adapterNovo.getFragment(0);
                            homeFragmentNovo.atualizaPostagens();

                        }else{
                            Toast.makeText(getApplicationContext(), "Erro ao postar imagem, tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deslogarUsuario(){
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void metodosParse(){

      //Testes de Métodos usados no Parse

      /*
      ParseObject pontuacao = new ParseObject("Pontuacao");
      pontuacao.put("nome", "Felipe");
      pontuacao.put("pontos", 150);
      pontuacao.saveInBackground();
      */

      ParseQuery<ParseObject> consulta = ParseQuery.getQuery("Pontuacao");
      consulta.getInBackground("7tbEcUE4WS", new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject object, ParseException e) {
              if(e == null){
                  object.put("pontos", 500);
                  object.saveInBackground();
              }else{
                  Log.i("consultaObjeto", "Erro ao consultar objeto");
              }
          }
      });

      ParseQuery<ParseObject> filtro = ParseQuery.getQuery("Pontuacao");
      //Aplicando filtro
      //filtro.whereGreaterThan("pontos", 160);
      filtro.whereGreaterThanOrEqualTo("pontos", 160);
      filtro.addAscendingOrder("pontos");

      //Listar os dados
      filtro.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {
              if(e == null){
                  Log.i("consultaObjeto", "Sucesso ao listar os objetos - " + objects.size());

                  for(ParseObject object : objects){
                      Log.i("ListarDados", "objetos - Nome: " + object.get("nome") + "pontos: " + object.get("pontos"));
                  }

              }else{
                  Log.i("consultaObjeto", "Erro ao listar os objetos - " + e.getMessage());
              }
          }
      });

      //Cadastro de usuários
      ParseUser usuario = new ParseUser();
      usuario.setUsername("feliperamos");
      usuario.setPassword("123456");
      usuario.setEmail("felipe@gmail.com");
      usuario.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
              if(e == null){
                  Log.i("cadastroUsuario", "Sucesso ao cadastrar usuário");
              }else{
                  Log.i("cadastroUsuario", "Erro ao cadastrar usuário - " + e.getMessage());
              }
          }
      });

      //Verificar usuario logado
      if(ParseUser.getCurrentUser()!= null){
          Log.i("LoginUsuario", "Usuário está logado");
      }else{
          Log.i("LoginUsuario", "Usuário não está logado");
      }

      //Fazer login
      ParseUser.logInInBackground("feliperamos", "123456", new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
              if(e == null){
                  Log.i("verificarLoginUsuario", "Sucesso ao logar usuário");
              }else{
                  Log.i("verificarLoginUsuario", "Erro ao logar usuário - " + e.getMessage());
              }
          }
      });
  }

}
