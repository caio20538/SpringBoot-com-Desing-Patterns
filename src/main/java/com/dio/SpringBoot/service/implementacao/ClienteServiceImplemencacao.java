package com.dio.SpringBoot.service.implementacao;

import com.dio.SpringBoot.model.Cliente;
import com.dio.SpringBoot.model.Endereco;
import com.dio.SpringBoot.repository.ClienteRepository;
import com.dio.SpringBoot.repository.EnderecoRepository;
import com.dio.SpringBoot.service.ClienteService;
import com.dio.SpringBoot.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImplemencacao implements ClienteService {
    //  Singleton: Injetar os componentes spring com @Autowired.
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;
    //  Strategy: Implementar os métodos definidos na interface.
    //  Facade: Abstrair integrações com subsistemas, provendo uma interface simples.


    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    private void salvarClienteComCep(Cliente cliente) {
        //verificar se existe o endereço do cliente via cep
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() ->{
            //caso n tenha, integrar viaCEP e persistir o retorno
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });

        cliente.setEndereco(endereco);

        //inserir o cliente
        clienteRepository.save(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        //buscar cliente pelo id
        Optional<Cliente> clientebd = clienteRepository.findById(id);

        if (clientebd.isPresent())
            salvarClienteComCep(cliente);
    }

    @Override
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
}
