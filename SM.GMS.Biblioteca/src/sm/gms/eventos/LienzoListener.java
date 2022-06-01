/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sm.gms.eventos;

import sm.gms.eventos.LienzoEvent;
import java.util.EventListener;

/**
 *
 * @author glori
 */
public interface LienzoListener extends EventListener {
    public void shapeAdded (LienzoEvent evt);
    public void propertyChange (LienzoEvent evt);
}
