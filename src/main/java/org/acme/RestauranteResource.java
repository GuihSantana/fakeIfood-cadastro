package org.acme;

import org.acme.entity.Prato;
import org.acme.entity.Restaurante;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestauranteResource {

    @GET
    public List<Restaurante> getRestaurantes(){
        return Restaurante.listAll();
    }

    @POST
    @Transactional
    public void adicionarRestaurante(Restaurante dto){
        dto.persist();
        Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public void atualizarRestaurante(@PathParam("id") Long id, Restaurante dto) {
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);
        if (restauranteOp.isEmpty()) {
            throw new NotFoundException();
        }
        Restaurante restaurante = restauranteOp.get();
        restaurante.name = dto.name;
        restaurante.persist();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void deleteRestaurante(@PathParam("id") Long id){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);
        restauranteOp.ifPresentOrElse(Restaurante::delete, ()-> {
            throw new NotFoundException();
        });
    }

    @GET
    @Path("{idRestaurante}/pratos")
    public List<Prato> getPratos(@PathParam("idRestaurante") Long idRestaurante){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if (restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não encontrado!");
        }
        return Prato.list("restaurante", restauranteOp.get());
    }

    @POST
    @Path("{idRestaurante}/pratos")
    @Transactional
    public Response adicionarPrato(@PathParam("idRestaurante") Long idRestaurante, Prato dto){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if (restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não encontrado!");
        }
        Prato prato = new Prato();
        prato.nome = dto.nome;
        prato.descricao = dto.descricao;

        prato.preco = dto.preco;
        prato.restaurante = restauranteOp.get();
        prato.persist();
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{idRestaurante}/pratos/{id}")
    @Transactional
    public void atualizarPrato (@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long idPrato, Prato dto){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if (restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não encontrado!");
        }

        Optional<Prato> pratoOp = Prato.findByIdOptional(idPrato);

        if (pratoOp.isEmpty()){
            throw new NotFoundException("Prato não encontrado");
        }

        Prato prato = pratoOp.get();
        prato.preco = dto.preco;
        prato.persist();
    }

    @DELETE
    @Path("{idRestaurante/pratos/{id}")
    @Transactional
    public void deletePrato(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long idPrato){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if (restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não encontrado!");
        }

        Optional<Prato> pratoOp = Prato.findByIdOptional(idPrato);

        pratoOp.ifPresentOrElse(Prato::delete, ()->{
            throw new NotFoundException("Prato não encontrado");
        });

    }
}
