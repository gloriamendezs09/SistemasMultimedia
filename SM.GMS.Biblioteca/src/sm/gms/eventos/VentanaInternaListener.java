/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sm.gms.eventos;

import java.util.EventListener;

/**
 *
 * @author glori
 */
public interface VentanaInternaListener extends EventListener{
    public void ventanaInternaChange (VentanaInternaEvent evt);
    public void mouseMoved (VentanaInternaEvent evt);
}
