/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;
import pojos.Nombreproducto;
import services.NombreproductoFacadeLocal;

/**
 *
 * @author Pelao
 */
@Named(value = "nombreProductoBean")
@SessionScoped
public class NombreProductoBean implements Serializable {

    @EJB
    private NombreproductoFacadeLocal nombreproductoFacade;

    private Nombreproducto nom;
    List<Nombreproducto> listita;

    public NombreProductoBean() {
        nom = new Nombreproducto();
        listita = nombreproductoFacade.findAll();
    }

    public List<Nombreproducto> getListita() {
        return listita;
    }

    public void setListita(List<Nombreproducto> listita) {
        this.listita = listita;
    }

    public Nombreproducto getNom() {
        return nom;
    }

    public void setNom(Nombreproducto nom) {
        this.nom = nom;
    }

    public List<Nombreproducto> getProductos() {
        return nombreproductoFacade.findAll();
    }

    public String crearNombreProducto() {
        try {
            
            Nombreproducto n = new Nombreproducto();
            n.setNombre(nom.getNombre());
            this.nombreproductoFacade.create(n);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto agregado al sistema!!"));
            nom = new Nombreproducto();
            return "añadirProductos";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al registrar Supervisor", ""));
            return "añadirProductos";
        }

    }

    public String eliminarSupervisor(Nombreproducto nom) {
        Nombreproducto n = nombreproductoFacade.find(nom.getId());
        nombreproductoFacade.remove(n);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Eliminado!!!"));
        return "gestionProductos";
    }

    public String actualizarProducto() {
        try {
            Nombreproducto n = nombreproductoFacade.find(nom.getId());
            n.setNombre(nom.getNombre());
            this.nombreproductoFacade.edit(n);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto editado!!"));
            return "gestionProductos";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al editar Producto", ""));
            return "gestionProductos";
        }
    }

    public void onRowEdit(RowEditEvent event) {
        try {
            for(Nombreproducto temp : listita)
            {
                this.nombreproductoFacade.edit(temp);
            }
            FacesMessage msg = new FacesMessage("Nombre Actualizado", ((Nombreproducto) event.getObject()).getNombre());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Nombre no Actualizado", ((Nombreproducto) event.getObject()).getNombre());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Editado Cancelado", ((Nombreproducto) event.getObject()).getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
