package app.freerouting.gui;

public class PopupMenuStitchRoute extends PopupMenuDisplay
{
    
    /** Creates a new instance of PopupMenuStitchRoute */
    public PopupMenuStitchRoute(BoardFrame p_board_frame)
    {
        super(p_board_frame);
        app.freerouting.board.LayerStructure layer_structure = board_panel.board_handling.get_routing_board().layer_structure;
        
        if (layer_structure.arr.length > 0)
        {
            change_layer_menu = new PopupMenuChangeLayer(p_board_frame);
            this.add(change_layer_menu, 0);
        }
        else
        {
            change_layer_menu = null;
        }
        java.util.ResourceBundle resources = 
                java.util.ResourceBundle.getBundle("app.freerouting.gui.Default", p_board_frame.get_locale());
        javax.swing.JMenuItem insert_item = new javax.swing.JMenuItem();
        insert_item.setText(resources.getString("insert"));
        insert_item.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                board_panel.board_handling.left_button_clicked(board_panel.right_button_click_location);
            }
        });
        
        this.add(insert_item, 0);
        
        javax.swing.JMenuItem done_item = new javax.swing.JMenuItem();
        done_item.setText(resources.getString("done"));
        done_item.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                board_panel.board_handling.return_from_state();
            }
        });
        
        this.add(done_item, 1);
        
        javax.swing.JMenuItem cancel_item = new javax.swing.JMenuItem();
        cancel_item.setText(resources.getString("cancel"));
        cancel_item.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                board_panel.board_handling.cancel_state();
            }
        });
        
        this.add(cancel_item, 2);
        
        app.freerouting.board.Layer curr_layer = layer_structure.arr[board_panel.board_handling.settings.get_layer()];
        disable_layer_item(layer_structure.get_signal_layer_no(curr_layer));
    }
    
    /**
     * Disables the p_no-th item in the change_layer_menu.
     */
    void disable_layer_item( int p_no)
    {
        if (this.change_layer_menu != null)
        {
            this.change_layer_menu.disable_item(p_no);
        }
    }
    
    private final PopupMenuChangeLayer change_layer_menu;
}
