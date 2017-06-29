/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.primefaces.event.RowEditEvent;
import pojos.Cliente;
import pojos.Producto;
import pojos.Vendedor;
import pojos.Precio;
import pojos.Nombreproducto;
import services.ClienteFacadeLocal;
import services.NombreproductoFacadeLocal;
import services.ProductoFacadeLocal;
import services.VendedorFacadeLocal;
import services.PrecioFacadeLocal;


/**
 *
 * @author Pelao
 */
@Named(value = "productoBean")
@SessionScoped
public class ProductoBean implements Serializable {
    
    @EJB
    private ClienteFacadeLocal clienteFacade;

    @EJB
    private ProductoFacadeLocal productoFacade;

    @EJB
    private NombreproductoFacadeLocal nomProdFacade;
    
    @EJB
    private VendedorFacadeLocal vendedorFacade;

    @EJB
    private PrecioFacadeLocal precioFacade;

    private Producto producto;
    private Cliente cliente;
    private int nombreID;
    private int precioID;
    private List<Producto> seleccionados;

    public ProductoBean() {
        producto = new Producto();
    }

    public Producto getProducto() {
        return producto;
    }

    public int getNombreID() {
        return nombreID;
    }

    public void setNombreID(int nombreID) {
        this.nombreID = nombreID;
    }

    public int getPrecioID() {
        return precioID;
    }

    public void setPrecioID(int precioID) {
        this.precioID = precioID;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getProductos() {
        return productoFacade.findAll();
    }
    
    public List<Nombreproducto> getNombreProductos() {
        return nomProdFacade.findAll();
    }
    
    public List<Producto> getProductosPendientes() {
        List<Producto> productos = productoFacade.findAll();
        List<Producto> pendientes = new ArrayList<Producto>();
        for (Producto temp : productos)
        {
            if(temp.getEstadoProducto().equals("Pendiente"))
            {
                pendientes.add(temp);
            }
        }
        return pendientes;
    }
    
    public List<Precio> getPrecios() {
        return precioFacade.findAll();
    }

    public String crearProducto() {
        try {
            Cliente c = (Cliente) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cliente");
            c = clienteFacade.find(c.getRutCliente());
            Producto p = new Producto();
            p.setNombreProducto(nomProdFacade.find(nombreID));
            p.setEstadoProducto("Pendiente");
            p.setRutCliente(c);
            p.setIdPrecio(precioFacade.find(precioID));
            asignarVendedor(p.getRutCliente());
            this.productoFacade.create(p);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto en espera de ser aprobado por un Supervisor"));
            return "seleccionProductos";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error, " + e.getMessage(), ""));
            return "seleccionProductos";
        }
    }
    
    public void asignarVendedor(Cliente cliente){
        Cliente c = clienteFacade.find(cliente.getRutCliente());
        List<Vendedor> listVendedores = vendedorFacade.findAll();
        int random = (int) (Math.random() * listVendedores.size());
        Vendedor v = listVendedores.get(random);
        c.setRutVendedor(v.getRutVendedor());
        clienteFacade.edit(c);
        
        final String username = "soportechileregional@gmail.com";
        final String password = "sgraskigivpstbeh";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(c.getCorreoCliente()));
            message.setSubject("Se le ha asignado un vendedor");
            message.setText("Estimado " + c.getNombresCliente() + " " + c.getApellidoPatCliente() + "\n"
                    + "Se le comunica que el vendedor que lo asistirá en todo momento en las compras de seguros en nuestra compañia es el Sr/a. " + v.getNombreVendedor() + "."+ "\n" + "\n"
                    + "Atte" + "\n"
                    + "Chile Regional");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        
    }

    public String eliminarProducto(Producto producto) {
        try {
            Producto p = productoFacade.find(producto);
            productoFacade.remove(p);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Eliminado!!!"));
            return "gestionProductos";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error, intente nuevamente", ""));
            return "gestionProductos";
        }
    }

    public String actualizarDatos() {
        Producto p = productoFacade.find(producto.getIdProducto());
        p.setNombreProducto(producto.getNombreProducto());
        productoFacade.edit(p);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto actualizado!!!"));
        return "gestionProductos";
    }

    public String aprobarCompra(Producto producto) {
        Cliente c = clienteFacade.find(producto.getRutCliente().getRutCliente());
        Producto p = productoFacade.find(producto.getIdProducto());
        p.setEstadoProducto("Aprobado");
        productoFacade.edit(p);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Aprobados!!!"));

        final String username = "soportechileregional@gmail.com";
        final String password = "sgraskigivpstbeh";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(c.getCorreoCliente()));
            message.setSubject("Su compra ha sido aprobada");
            message.setText("Estimado " + c.getNombresCliente() + " " + c.getApellidoPatCliente() + "\n"
                    + "Se le comunica que la compra de su producto " + p.getNombreProducto().getNombre() + " ha sido aprobada, se le recuerda también que el pago de la primera cuota se ha vencido y por ende a quedado en morosidad."+ "\n" + "\n"
                    + "Atte" + "\n"
                    + "Chile Regional");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "aprobarProductos";
    }

}
