package com.example.trabalho3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.trabalho3.modelos.Produto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editNome, editDescricao;
    private ListView listProdutos;
    Produto produtoSelecionado;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Produto> produtoList = new ArrayList<Produto>();
    private ArrayAdapter<Produto> produtoArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializandoComponentes();
        inicializandoFirebase();
        eventoDataBase();
        inicializarList();
    }

    private void addProduto() {
        Produto p = new Produto();
        p.setNome(editNome.getText().toString());
        p.setDescricao(editDescricao.getText().toString());
        databaseReference.child("Produto").child(p.getId()).setValue(p);
        Toast.makeText(this,p.getNome() + " adicionado com sucesso ",Toast.LENGTH_LONG).show();
        editNome.setText("");
        editDescricao.setText("");
    }

    private void deleteProduto() {
        Produto p = new Produto();
        p.setId(produtoSelecionado.getId());
        databaseReference.child("Produto").child(p.getId()).removeValue();
        Toast.makeText(this," produto foi removido com sucesso",Toast.LENGTH_LONG).show();
        editNome.setText("");
        editDescricao.setText("");
    }

    private void updateProduto() {
        Produto p = new Produto();
        p.setId(produtoSelecionado.getId());
        p.setNome(editNome.getText().toString());
        p.setDescricao(editDescricao.getText().toString());
        databaseReference.child("Produto").child(p.getId()).setValue(p);
        //exibe msg
        Toast.makeText(this," foi atualizado com sucesso",Toast.LENGTH_LONG).show();
        //limpa campos
        editDescricao.setText("");
        editNome.setText("");
    }

    private void inicializandoFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializandoComponentes() {
        editNome = (EditText)findViewById(R.id.editNome);
        editDescricao = (EditText)findViewById(R.id.editDescricao);
        listProdutos = (ListView) findViewById(R.id.listViewPessoas);
    }

    private void inicializarList() {
        listProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                produtoSelecionado = (Produto) adapterView.getItemAtPosition(i);
                editNome.setText(produtoSelecionado.getNome());
                editDescricao.setText(produtoSelecionado.getDescricao());
            }
        });
    }

    private void eventoDataBase() {
        databaseReference.child("Produto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtoList.clear();
                for (DataSnapshot objetoSnapshot : dataSnapshot.getChildren()){
                    Produto p = objetoSnapshot.getValue(Produto.class);
                    produtoList.add(p);
                }
                produtoArrayAdapter = new ArrayAdapter<Produto>(MainActivity.this,
                        android.R.layout.simple_list_item_1,produtoList);
                listProdutos.setAdapter(produtoArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemSalvar:
                addProduto();
                break;
            case R.id.itemAtualizar:
                updateProduto();
                break;
            case R.id.itemDeletar:
                deleteProduto();
                break;
            default:
                Toast.makeText(this,"Escolha um Opção",Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
    //fim menu
}