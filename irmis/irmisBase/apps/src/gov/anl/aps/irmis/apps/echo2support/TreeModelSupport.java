/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.echo2support;

import echopointng.tree.TreeModelListener;
import echopointng.tree.TreeModelEvent;
import java.util.*;

public class TreeModelSupport {
   private Vector vector = new Vector();

   public void addTreeModelListener( TreeModelListener listener ) {
      if ( listener != null && !vector.contains( listener ) ) {
         vector.addElement( listener );
      }
   }

   public void removeTreeModelListener( TreeModelListener listener ) {
      if ( listener != null ) {
         vector.removeElement( listener );
      }
   }

   public void fireTreeNodesChanged( TreeModelEvent e ) {
      Enumeration listeners = vector.elements();
      while ( listeners.hasMoreElements() ) {
         TreeModelListener listener = (TreeModelListener)listeners.nextElement();
         listener.treeNodesChanged( e );
      }
   }

   public void fireTreeNodesInserted( TreeModelEvent e ) {
      Enumeration listeners = vector.elements();
      while ( listeners.hasMoreElements() ) {
         TreeModelListener listener = (TreeModelListener)listeners.nextElement();
         listener.treeNodesInserted( e );
      }
   }

   public void fireTreeNodesRemoved( TreeModelEvent e ) {
      Enumeration listeners = vector.elements();
      while ( listeners.hasMoreElements() ) {
         TreeModelListener listener = (TreeModelListener)listeners.nextElement();
         listener.treeNodesRemoved( e );
      }
   }

   public void fireTreeStructureChanged( TreeModelEvent e ) {
      Enumeration listeners = vector.elements();
      while ( listeners.hasMoreElements() ) {
         TreeModelListener listener = (TreeModelListener)listeners.nextElement();
         listener.treeStructureChanged( e );
      }
   }
}