/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;

import java.util.List;


public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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