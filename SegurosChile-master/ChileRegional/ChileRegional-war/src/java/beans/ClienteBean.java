/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import pojos.Cliente;
import pojos.Vendedor;
import services.ClienteFacadeLocal;
import services.VendedorFacadeLocal;

/**
 *
 * @author Pelao
 */
@Named(value = "clienteBean")
@SessionScoped
public class ClienteBean implements Serializable {

    @EJB
    private VendedorFacadeLocal vendedorFacade;

    @EJB
    private ClienteFacadeLocal clienteFacade;

    private Cliente cliente;
    private Vendedor vendedor;
    boolean loggedIn = false;

    public ClienteBean() {
        cliente = new Cliente();
        vendedor = new Vendedor();
    }

    public VendedorFacadeLocal getVendedorFacade() {
        return vendedorFacade;
    }

    public void setVendedorFacade(VendedorFacadeLocal vendedorFacade) {
        this.vendedorFacade = vendedorFacade;
    }

    public ClienteFacadeLocal getClienteFacade() {
        return clienteFacade;
    }

    public void setClienteFacade(ClienteFacadeLocal clienteFacade) {
        this.clienteFacade = clienteFacade;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public List<Cliente> getClientes() {
        return clienteFacade.findAll();
    }

    public void login(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = null;
        Cliente c = clienteFacade.find(cliente.getRutCliente());

        if (c != null && cliente.getClaveCliente() == c.getClaveCliente()) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", c.getNombresCliente() + " " + c.getApellidoPatCliente());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cliente", c);
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.addCallbackParam("loggedIn", loggedIn);
            context.addCallbackParam("view", "faces/indexCliente.xhtml");
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Rut o clave no válida");
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.addCallbackParam("view", "faces/loginCliente.xhtml");

        }
    }

    public String getNombresito(){
        Cliente c = (Cliente) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cliente");
        c = clienteFacade.find(c.getRutCliente());
        return c.getNombresCliente() + " " + c.getApellidoPatCliente();
    }
    
    public boolean verificarSesionMenu() {
        FacesContext context = FacesContext.getCurrentInstance();
        Cliente c1 = (Cliente) context.getExternalContext().getSessionMap().get("cliente");
        if (c1 == null) {
            return false;
        } else {
            return true;
        }
    }

    public void verificarSesion() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Cliente c = (Cliente) context.getExternalContext().getSessionMap().get("cliente");
            if (c == null) {
                context.getExternalContext().redirect("faces/index.xhtml");
            }
        } catch (Exception e) {
            //log
        }
    }

    public void cerrarSesion() {
        ExternalContext ctx
                = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath
                = ((ServletContext) ctx.getContext()).getContextPath();
        try {
            ((HttpSession) ctx.getSession(false)).invalidate();
            ctx.redirect(ctxPath + "/faces/index.xhtml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String crearCliente() {
        try {
            Cliente c = new Cliente();
            verificarRut();
            c.setRutCliente(cliente.getRutCliente());
            c.setDvCliente(cliente.getDvCliente());
            c.setClaveCliente(crearContrasena());
            c.setNombresCliente(cliente.getNombresCliente());
            c.setApellidoPatCliente(cliente.getApellidoPatCliente());
            c.setApellidoMatCliente(cliente.getApellidoMatCliente());
            c.setDireccionCliente(cliente.getDireccionCliente());
            c.setTelefonoCliente(cliente.getTelefonoCliente());
            c.setCorreoCliente(cliente.getCorreoCliente());
            c.setActividad(cliente.getActividad());
            c.setBeneficiario1Nombre(cliente.getBeneficiario1Nombre());
            c.setBeneficiario2Nombre(cliente.getBeneficiario2Nombre());
            this.clienteFacade.create(c);
            enviarMail();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Se le ha enviado un correo electrónico con su clave para entrar al sistema"));
            cliente = new Cliente();
            return "loginCliente";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error, " + e.getMessage(), ""));
            return "registrarCliente";
        }

    }

    private void verificarRut() throws Exception {
        Cliente c = clienteFacade.find(cliente.getRutCliente());
        if (c != null) {
            throw new Exception("Ese rut ya está registrado");
        }
    }

    private int crearContrasena() {
        int password = Integer.parseInt(cliente.getRutCliente().toString() + "8");
        return password;
    }

    public void enviarMail() {

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
                    InternetAddress.parse(cliente.getCorreoCliente()));
            message.setSubject("Contraseña para ingresar a nuestra página web");
            message.setText("Estimado " + cliente.getNombresCliente() + " " + cliente.getApellidoPatCliente() + "\n"
                    + "Su contraseña para ingresar al sistema es: " + crearContrasena() + ". Se le recuerda que puede cambiarla una vez ingresado al sistema en el menú de Cambiar contraseña." + "\n" + "\n"
                    + "Atte" + "\n"
                    + "Chile Regional");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String eliminarCliente(Cliente cliente) {
        try {
            Cliente c = clienteFacade.find(cliente);
            clienteFacade.remove(c);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente Eliminado!!!"));
            return "gestionarClientes";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error, intente nuevamente", ""));
            return "gestionarClientes";
        }

    }

    public String actualizarDatos() {
        Cliente c = clienteFacade.find(cliente);
        c.setDireccionCliente(cliente.getDireccionCliente());
        c.setTelefonoCliente(cliente.getTelefonoCliente());
        c.setCorreoCliente(cliente.getCorreoCliente());
        c.setActividad(cliente.getActividad());
        clienteFacade.edit(c);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente actualizado!!!"));
        return "gestionarClientes";
    }

    public String actualizarContrasena() {
        Cliente c = (Cliente) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cliente");
        c = clienteFacade.find(c.getRutCliente());
        if (c.getClaveCliente() == cliente.getClaveCliente()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No debe usar la contraseña antigua", ""));
            return "cambioContrasenaCliente";
        } else {
            c.setClaveCliente(cliente.getClaveCliente());
            clienteFacade.edit(c);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Su contraseña ha sido modificada"));
            cerrarSesion();
            return "loginCliente";
        }
    }

    //Generar menu
    private MenuModel menu;

    public MenuModel generarMenu() {
        menu = new DefaultMenuModel();
        Cliente pCliente = (Cliente) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cliente");
        if (pCliente != null) {
            this.cliente = pCliente;
            DefaultMenuItem item = new DefaultMenuItem("INICIO");
            item.setOutcome("indexCliente");
            menu.addElement(item);

            item = new DefaultMenuItem("SOLICITAR SEGURO");
            item.setOutcome("seleccionProductos");
            menu.addElement(item);

            item = new DefaultMenuItem("CAMBIAR CONTRASEÑA");
            item.setOutcome("cambioContrasenaCliente");
            menu.addElement(item);

        }

        return menu;
    }

}
