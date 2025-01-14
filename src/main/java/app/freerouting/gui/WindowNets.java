package app.freerouting.gui;

import app.freerouting.rules.Net;
import app.freerouting.rules.Nets;

import java.util.List;

public class WindowNets extends WindowObjectListWithFilter
{
    
    /** Creates a new instance of NetsWindow */
    public WindowNets(BoardFrame p_board_frame)
    {
        super(p_board_frame);
        this.resources = java.util.ResourceBundle.getBundle("app.freerouting.gui.WindowNets", p_board_frame.get_locale());
        this.setTitle(resources.getString("title"));
        
        javax.swing.JPanel curr_button_panel =  new javax.swing.JPanel();
        this.south_panel.add(curr_button_panel, java.awt.BorderLayout.NORTH);
        
        final javax.swing.JButton  assign_class_button = new javax.swing.JButton(resources.getString("assign_class"));
        curr_button_panel.add(assign_class_button);
        assign_class_button.setToolTipText(resources.getString("assign_class_tooltip"));
        assign_class_button.addActionListener(new AssignClassListener());
        
        final javax.swing.JButton  filter_incompletes_button = new javax.swing.JButton(resources.getString("filter_incompletes"));
        curr_button_panel.add(filter_incompletes_button);
        filter_incompletes_button.setToolTipText(resources.getString("filter_incompletes_tooltip"));
        filter_incompletes_button.addActionListener(new FilterIncompletesListener());
        p_board_frame.set_context_sensitive_help(this, "WindowObjectList_Nets");
    }
    
    /**
     * Fills the list with the nets in the net list.
     */
    protected void fill_list()
    {
        Nets nets = this.board_frame.board_panel.board_handling.get_routing_board().rules.nets;
        Net[] sorted_arr = new Net[nets.max_net_no()];
        for (int i = 0; i < sorted_arr.length; ++i)
        {
            sorted_arr[i] = nets.get(i + 1);
        }
        java.util.Arrays.sort(sorted_arr);
        for (int i = 0; i < sorted_arr.length; ++i)
        {
            this.add_to_list(sorted_arr[i]);
        }
        this.list.setVisibleRowCount(Math.min(sorted_arr.length, DEFAULT_TABLE_SIZE));
    }
    
    protected void select_instances()
    {
        List<Object> selected_nets = list.getSelectedValuesList();
        if (selected_nets.size() <= 0)
        {
            return;
        }
        int [] selected_net_numbers = new int[selected_nets.size()];
        for (int i = 0; i < selected_nets.size(); ++i)
        {
            selected_net_numbers[i] = ((Net) selected_nets.get(i)).net_number;
        }
        app.freerouting.board.RoutingBoard routing_board = board_frame.board_panel.board_handling.get_routing_board();
        java.util.Set<app.freerouting.board.Item> selected_items = new java.util.TreeSet<app.freerouting.board.Item>();
        java.util.Collection<app.freerouting.board.Item> board_items = routing_board.get_items();
        for (app.freerouting.board.Item curr_item : board_items)
        {
            boolean item_matches = false;
            for (int curr_net_no : selected_net_numbers)
            {
                if (curr_item.contains_net(curr_net_no))
                {
                    item_matches = true;
                    break;
                }
            }
            if (item_matches)
            {
                selected_items.add(curr_item);
            }
        }
        board_frame.board_panel.board_handling.select_items(selected_items);
        board_frame.board_panel.board_handling.zoom_selection();
    }
    
    private final java.util.ResourceBundle resources;
    
    private class AssignClassListener implements java.awt.event.ActionListener
    {
        public void actionPerformed(java.awt.event.ActionEvent p_evt)
        {
            List<Object> selected_nets = list.getSelectedValuesList();
            if (selected_nets.size() <= 0)
            {
                return;
            }
            app.freerouting.rules.NetClasses net_classes = board_frame.board_panel.board_handling.get_routing_board().rules.net_classes;
            app.freerouting.rules.NetClass [] class_arr = new app.freerouting.rules.NetClass [net_classes.count()];
            for(int i = 0; i < class_arr.length; ++i)
            {
                class_arr[i] = net_classes.get(i);
            }
            Object selected_value = javax.swing.JOptionPane.showInputDialog(null, resources.getString("message_1"), 
                    resources.getString("message_2"), javax.swing.JOptionPane.INFORMATION_MESSAGE, 
                    null, class_arr, class_arr[0]);
            if (!(selected_value instanceof app.freerouting.rules.NetClass))
            {
                return;
            }
            app.freerouting.rules.NetClass selected_class = (app.freerouting.rules.NetClass) selected_value;
            for (int i = 0; i < selected_nets.size(); ++i)
            {
                ((Net) selected_nets.get(i)).set_class(selected_class);
            }
            board_frame.refresh_windows();
        }
    }
    
    private class FilterIncompletesListener implements java.awt.event.ActionListener
    {
        public void actionPerformed(java.awt.event.ActionEvent p_evt)
        {
            List<Object> selected_nets = list.getSelectedValuesList();
            if (selected_nets.size() <= 0)
            {
                return;
            }
            app.freerouting.interactive.BoardHandling board_handling = board_frame.board_panel.board_handling;
            int max_net_no = board_handling.get_routing_board().rules.nets.max_net_no();
            for (int i = 1; i <= max_net_no; ++i)
            {
                board_handling.set_incompletes_filter(i, true);
            }
            for (int i = 0; i < selected_nets.size(); ++i)
            {
                board_handling.set_incompletes_filter(((Net) selected_nets.get(i)).net_number, false);
            }
            board_frame.board_panel.repaint();
        }
    }
}
