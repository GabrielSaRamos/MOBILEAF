package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Livro;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText edtTitulo, edtAutor, edtBiblioteca;

    private Button btnSalvar;
    private ListView listViewLivros;

    private DatabaseReference databaseReference;
    private ArrayList<Livro> listaLivros = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> titulosLivros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        btnSalvar = findViewById(R.id.btnSalvar);
        listViewLivros = findViewById(R.id.listViewLivros);
        edtBiblioteca = findViewById(R.id.edtBiblioteca);

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("livros");

        btnSalvar.setOnClickListener(view -> salvarLivro());

        // Inicializa o adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulosLivros);
        listViewLivros.setAdapter(adapter);

        carregarLivros();

        listViewLivros.setOnItemClickListener((adapterView, view, position, id) -> {
            Livro livroSelecionado = listaLivros.get(position);

            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Excluir livro")
                    .setMessage("Deseja excluir o livro \"" + livroSelecionado.getTitulo() + "\"?")
                    .setPositiveButton("Sim", (dialog, which) -> deletarLivroComLog(livroSelecionado))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    private void salvarLivro() {
        String titulo = edtTitulo.getText().toString().trim();
        String autor = edtAutor.getText().toString().trim();
        String biblioteca = edtBiblioteca.getText().toString().trim();

        if (titulo.isEmpty() || autor.isEmpty() || biblioteca.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Livro livro = new Livro(id, titulo, autor, biblioteca);
        databaseReference.child(id).setValue(livro).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Livro salvo com sucesso!", Toast.LENGTH_SHORT).show();
                edtTitulo.setText("");
                edtAutor.setText("");
                edtBiblioteca.setText("");
            } else {
                Toast.makeText(this, "Erro ao salvar livro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarLivros() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaLivros.clear();
                titulosLivros.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Livro livro = ds.getValue(Livro.class);
                    if (livro != null) {
                        listaLivros.add(livro);
                        titulosLivros.add(livro.getTitulo() + " - " + livro.getAutor() + " (" + livro.getBiblioteca() + ")");
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Erro ao carregar livros", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletarLivroComLog(Livro livro) {
        String idLivro = livro.getId();
        String titulo = livro.getTitulo();
        String nomeBiblioteca = "Biblioteca Central";

        databaseReference.child(idLivro).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Livro deletado com sucesso!", Toast.LENGTH_SHORT).show();

                // Grava o log
                String dataHora = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
                DatabaseReference logRef = FirebaseDatabase.getInstance().getReference("logs_deletes");

                String logId = logRef.push().getKey();
                if (logId != null) {
                    Map<String, Object> log = new java.util.HashMap<>();
                    log.put("titulo", titulo);
                    log.put("biblioteca", nomeBiblioteca);
                    log.put("dataHora", dataHora);

                    logRef.child(logId).setValue(log);
                }

            } else {
                Toast.makeText(MainActivity.this, "Erro ao deletar livro", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
