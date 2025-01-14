package app.freerouting.designforms.specctra;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import app.freerouting.geometry.planar.PolylineShape;
import app.freerouting.datastructures.IndentFileWriter;
import app.freerouting.datastructures.IdentifierType;
import app.freerouting.logger.FRLogger;

/**
 * Describes a shape in a Specctra dsn file.
 */
public abstract class Shape
{

    /**
     * Writes a shape scope to a Specctra dsn file.
     */
    public abstract void write_scope(IndentFileWriter p_file, IdentifierType p_identifier) throws java.io.IOException;

    /**
     * Writes a shape scope to a Specctra session file.
     * In a session file all coordinates must be integer.
     */
    public abstract void write_scope_int(IndentFileWriter p_file, IdentifierType p_identifier) throws java.io.IOException;

    /**
     * Reads shape scope from a Specctra dsn file.
     * If p_layer_structure == null, only Layer.PCB and Layer.Signal are expected, no induvidual layers.
     */
    public static Shape read_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        Shape result = null;
        try
        {
            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.OPEN_BRACKET)
            {
                // overread the open bracket
                next_token = p_scanner.next_token();
            }

            if (next_token == Keyword.RECTANGLE)
            {

                result = Shape.read_rectangle_scope(p_scanner, p_layer_structure);
            }
            else if (next_token == Keyword.POLYGON)
            {

                result = Shape.read_polygon_scope(p_scanner, p_layer_structure);
            }
            else if (next_token == Keyword.CIRCLE)
            {

                result = Shape.read_circle_scope(p_scanner, p_layer_structure);
            }
            else if (next_token == Keyword.POLYGON_PATH)
            {
                result = Shape.read_polygon_path_scope(p_scanner, p_layer_structure);
            }
            else
            {
                // not a shape scope, skip it.
                ScopeKeyword.skip_scope(p_scanner);
            }
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("Shape.read_scope: IO error scanning file", e);
            return result;
        }
        return result;
    }

    /**
     * Reads an object of type PolylinePath from the dsn-file.
     */
    public static PolylinePath read_polyline_path_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        try
        {
            Layer layer = null;
            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.PCB_SCOPE)
            {
                layer = Layer.PCB;
            }
            else if (next_token == Keyword.SIGNAL)
            {
                layer = Layer.SIGNAL;
            }
            else
            {
                if (p_layer_structure == null)
                {
                    FRLogger.warn("PolylinePath.read_scope: only layer types pcb or signal expected");
                    return null;
                }
                if (!(next_token instanceof String))
                {
                    FRLogger.warn("PolylinePath.read_scope: layer name string expected");
                    return null;
                }
                int layer_no = p_layer_structure.get_no((String) next_token);
                if (layer_no < 0 || layer_no >= p_layer_structure.arr.length)
                {
                    FRLogger.warn("Shape.read_polyline_path_scope: layer name '" + next_token + "' not found in layer structure ");
                    return null;
                }
                layer = p_layer_structure.arr[layer_no];
            }
            Collection<Object> corner_list = new LinkedList<Object>();

            // read the width and the corners of the path
            for (;;)
            {
                next_token = p_scanner.next_token();
                if (next_token == Keyword.CLOSED_BRACKET)
                {
                    break;
                }
                corner_list.add(next_token);
            }
            if (corner_list.size() < 5)
            {
                FRLogger.warn("PolylinePath.read_scope: to few numbers in scope");
                return null;
            }
            Iterator<Object> it = corner_list.iterator();
            double width = 0;
            Object next_object = it.next();
            if (next_object instanceof Double)
            {
                width = ((Double) next_object).doubleValue();
            }
            else if (next_object instanceof Integer)
            {
                width = ((Integer) next_object).intValue();
            }
            else
            {
                FRLogger.warn("PolylinePath.read_scope: number expected");
                return null;
            }
            double[] corner_arr = new double[corner_list.size() - 1];
            for (int i = 0; i < corner_arr.length; ++i)
            {
                next_object = it.next();
                if (next_object instanceof Double)
                {
                    corner_arr[i] = ((Double) next_object).doubleValue();
                }
                else if (next_object instanceof Integer)
                {
                    corner_arr[i] = ((Integer) next_object).intValue();
                }
                else
                {
                    FRLogger.warn("Shape.read_polygon_path_scope: number expected");
                    return null;
                }

            }
            return new PolylinePath(layer, width, corner_arr);
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("PolylinePath.read_scope: IO error scanning file", e);
            return null;
        }
    }

    /**
     * Reads a shape , which may contain holes from a specctra dsn-file.
     * The first shape in the shape_list of the result is the border of the area.
     * The other shapes in the shape_list are holes (windows).
     */
    public static ReadAreaScopeResult read_area_scope(Scanner p_scanner,
                                                      LayerStructure p_layer_structure, boolean p_skip_window_scopes)
    {
        Collection<Shape> shape_list = new LinkedList<Shape>();
        String clearance_class_name = null;
        String area_name = null;
        boolean result_ok = true;
        Object next_token = null;
        try
        {
            next_token = p_scanner.next_token();
        }
        catch (java.io.IOException e)
        {
            FRLogger.warn("Shape.read_area_scope: IO error scanning file");
            return null;
        }
        if (next_token instanceof String)
        {
            String curr_name = (String) next_token;
            if (!curr_name.isEmpty())
            {
                area_name = curr_name;
            }
        }
        Shape curr_shape = Shape.read_scope(p_scanner, p_layer_structure);
        if (curr_shape == null)
        {
            result_ok = false;
        }
        shape_list.add(curr_shape);
        next_token = null;
        for (;;)
        {
            Object prev_token = next_token;
            try
            {
                next_token = p_scanner.next_token();
            }
            catch (java.io.IOException e)
            {
                FRLogger.error("Shape.read_area_scope: IO error scanning file", e);
                return null;
            }
            if (next_token == null)
            {
                FRLogger.warn("Shape.read_area_scope: unexpected end of file");
                return null;
            }
            if (next_token == Keyword.CLOSED_BRACKET)
            {
                // end of scope
                break;
            }

            if (prev_token == Keyword.OPEN_BRACKET)
            {
                // a new scope is expected
                if (next_token == Keyword.WINDOW && !p_skip_window_scopes)
                {
                    Shape hole_shape = Shape.read_scope(p_scanner, p_layer_structure);
                    shape_list.add(hole_shape);
                    // overread closing bracket
                    try
                    {
                        next_token = p_scanner.next_token();
                    }
                    catch (java.io.IOException e)
                    {
                        FRLogger.error("Shape.read_area_scope: IO error scanning file", e);
                        return null;
                    }
                    if (next_token != Keyword.CLOSED_BRACKET)
                    {
                        FRLogger.warn("Shape.read_area_scope: closed bracket expected");
                        return null;
                    }

                }
                else if (next_token == Keyword.CLEARANCE_CLASS)
                {
                    clearance_class_name = DsnFile.read_string_scope(p_scanner);
                }
                else
                {
                    // skip unknown scope
                    ScopeKeyword.skip_scope(p_scanner);
                }
            }
        }
        if (!result_ok)
        {
            return null;
        }
        return new ReadAreaScopeResult(area_name, shape_list, clearance_class_name);
    }

    /**
     * Reads a rectangle scope from a Specctra dsn file.
     * If p_layer_structure == null, only Layer.PCB and Layer.Signal are expected, no induvidual layers.
     */
    public static Rectangle read_rectangle_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        try
        {
            Layer rect_layer = null;
            double[] rect_coor = new double[4];

            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.PCB_SCOPE)
            {
                rect_layer = Layer.PCB;
            }
            else if (next_token == Keyword.SIGNAL)
            {
                rect_layer = Layer.SIGNAL;
            }
            else if (p_layer_structure != null)
            {
                if (!(next_token instanceof String))
                {
                    FRLogger.warn("Shape.read_rectangle_scope: layer name string expected");
                    return null;
                }
                String layer_name = (String) next_token;
                int layer_no = p_layer_structure.get_no(layer_name);
                if (layer_no < 0 || layer_no >= p_layer_structure.arr.length)
                {
                    FRLogger.warn("Shape.read_rectangle_scope: layer name " + layer_name + " not found in layer structure ");
                }
                else
                {
                    rect_layer = p_layer_structure.arr[layer_no];
                }
            }
            else
            {
                rect_layer = Layer.SIGNAL;
            }
            // fill the the rectangle
            for (int i = 0; i < 4; ++i)
            {
                next_token = p_scanner.next_token();
                if (next_token instanceof Double)
                {
                    rect_coor[i] = ((Double) next_token).doubleValue();
                }
                else if (next_token instanceof Integer)
                {
                    rect_coor[i] = ((Integer) next_token).intValue();
                }
                else
                {
                    FRLogger.warn("Shape.read_rectangle_scope: number expected");
                    return null;
                }
            }
            // overread the closing bracket

            next_token = p_scanner.next_token();
            if (next_token != Keyword.CLOSED_BRACKET)
            {
                FRLogger.warn("Shape.read_rectangle_scope ) expected");
                return null;
            }
            if (rect_layer == null)
            {
                return null;
            }
            return new Rectangle(rect_layer, rect_coor);
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("Shape.read_rectangle_scope: IO error scanning file", e);
            return null;
        }
    }

    /**
     * Reads a closed polygon scope from a Specctra dsn file.
     * If p_layer_structure == null, only Layer.PCB and Layer.Signal are expected, no induvidual layers.
     */
    public static Polygon read_polygon_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        try
        {
            Layer polygon_layer = null;
            boolean layer_ok = true;
            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.PCB_SCOPE)
            {
                polygon_layer = Layer.PCB;
            }
            else if (next_token == Keyword.SIGNAL)
            {
                polygon_layer = Layer.SIGNAL;
            }
            else
            {
                if (p_layer_structure == null)
                {
                    FRLogger.warn("Shape.read_polygon_scope: only layer types pcb or signal expected");
                    return null;
                }
                if (!(next_token instanceof String))
                {
                    FRLogger.warn("Shape.read_polygon_scope: layer name string expected");
                    return null;
                }
                int layer_no = p_layer_structure.get_no((String) next_token);
                if (layer_no < 0 || layer_no >= p_layer_structure.arr.length)
                {
                    FRLogger.warn("Shape.read_polygon_scope: layer name '" + next_token + "' not found in layer structure ");
                    layer_ok = false;
                }
                else
                {
                    polygon_layer = p_layer_structure.arr[layer_no];
                }
            }

            // overread the aperture width
            next_token = p_scanner.next_token();

            Collection<Object> coor_list = new LinkedList<Object>();

            // read the coordinates of the polygon
            for (;;)
            {
                next_token = p_scanner.next_token();
                if (next_token == null)
                {
                    FRLogger.warn("Shape.read_polygon_scope: unexpected end of file");
                    return null;
                }
                if (next_token == Keyword.OPEN_BRACKET)
                {
                    // unknown scope
                    ScopeKeyword.skip_scope(p_scanner);
                    next_token = p_scanner.next_token();
                }
                if (next_token == Keyword.CLOSED_BRACKET)
                {
                    break;
                }
                coor_list.add(next_token);
            }
            if (!layer_ok)
            {
                return null;
            }
            double[] coor_arr = new double[coor_list.size()];
            Iterator<Object> it = coor_list.iterator();
            for (int i = 0; i < coor_arr.length; ++i)
            {
                Object next_object = it.next();
                if (next_object instanceof Double)
                {
                    coor_arr[i] = ((Double) next_object).doubleValue();
                }
                else if (next_object instanceof Integer)
                {
                    coor_arr[i] = ((Integer) next_object).intValue();
                }
                else
                {
                    FRLogger.warn("Shape.read_polygon_scope: number expected");
                    return null;
                }

            }
            return new Polygon(polygon_layer, coor_arr);
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("Rectangle.read_scope: IO error scanning file", e);
            return null;
        }
    }

    /**
     * Reads a circle scope from a Specctra dsn file.
     */
    public static Circle read_circle_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        try
        {
            Layer circle_layer = null;
            boolean layer_ok = true;
            double[] circle_coor = new double[3];

            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.PCB_SCOPE)
            {
                circle_layer = Layer.PCB;
            }
            else if (next_token == Keyword.SIGNAL)
            {
                circle_layer = Layer.SIGNAL;
            }
            else
            {
                if (p_layer_structure == null)
                {
                    FRLogger.warn("Shape.read_circle_scope: p_layer_structure != null expected");
                    return null;
                }
                if (!(next_token instanceof String))
                {
                    FRLogger.warn("Shape.read_circle_scope: string for layer_name expected");
                    return null;
                }
                int layer_no = p_layer_structure.get_no((String) next_token);
                if (layer_no < 0 || layer_no >= p_layer_structure.arr.length)
                {
                    FRLogger.warn("Shape.read_circle_scope: layer with name '" + next_token + "' not found in layer stracture ");
                    layer_ok = false;
                }
                else
                {
                    circle_layer = p_layer_structure.arr[layer_no];
                }
            }
            // fill the the the coordinates
            int curr_index = 0;
            for (;;)
            {
                next_token = p_scanner.next_token();
                if (next_token == Keyword.CLOSED_BRACKET)
                {
                    break;
                }
                if (curr_index > 2)
                {
                    FRLogger.warn("Shape.read_circle_scope: closed bracket expected");
                    return null;
                }
                if (next_token instanceof Double)
                {
                    circle_coor[curr_index] = ((Double) next_token).doubleValue();
                }
                else if (next_token instanceof Integer)
                {
                    circle_coor[curr_index] = ((Integer) next_token).intValue();
                }
                else
                {
                    FRLogger.warn("Shape.read_circle_scope: number expected");
                    return null;
                }
                ++curr_index;
            }
            if (!layer_ok)
            {
                return null;
            }
            return new Circle(circle_layer, circle_coor);
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("Shape.read_rectangle_scope: IO error scanning file", e);
            return null;
        }
    }

    /**
     * Reads an object of type Path from the dsn-file.
     */
    public static PolygonPath read_polygon_path_scope(Scanner p_scanner, LayerStructure p_layer_structure)
    {
        try
        {
            Layer layer = null;
            boolean layer_ok = true;
            Object next_token = p_scanner.next_token();
            if (next_token == Keyword.PCB_SCOPE)
            {
                layer = Layer.PCB;
            }
            else if (next_token == Keyword.SIGNAL)
            {
                layer = Layer.SIGNAL;
            }
            else
            {
                if (p_layer_structure == null)
                {
                    FRLogger.warn("Shape.read_polygon_path_scope: only layer types pcb or signal expected");
                    return null;
                }
                if (!(next_token instanceof String))
                {
                    FRLogger.warn("Path.read_scope: layer name string expected");
                    return null;
                }
                int layer_no = p_layer_structure.get_no((String) next_token);
                if (layer_no < 0 || layer_no >= p_layer_structure.arr.length)
                {
                    FRLogger.warn("Shape.read_polygon_path_scope: layer with name '" + next_token + "' not found in layer structure ");
                    layer_ok = false;
                }
                else
                {
                    layer = p_layer_structure.arr[layer_no];
                }
            }
            Collection<Object> corner_list = new LinkedList<Object>();

            // read the width and the corners of the path
            for (;;)
            {
                next_token = p_scanner.next_token();
                if (next_token == Keyword.OPEN_BRACKET)
                {
                    // unknown scope
                    ScopeKeyword.skip_scope(p_scanner);
                    next_token = p_scanner.next_token();
                }
                if (next_token == Keyword.CLOSED_BRACKET)
                {
                    break;
                }
                corner_list.add(next_token);
            }
            if (corner_list.size() < 5)
            {
                FRLogger.warn("Shape.read_polygon_path_scope: to few numbers in scope");
                return null;
            }
            if (!layer_ok)
            {
                return null;
            }
            Iterator<Object> it = corner_list.iterator();
            double width = 0;
            Object next_object = it.next();
            if (next_object instanceof Double)
            {
                width = ((Double) next_object).doubleValue();
            }
            else if (next_object instanceof Integer)
            {
                width = ((Integer) next_object).intValue();
            }
            else
            {
                FRLogger.warn("Shape.read_polygon_path_scope: number expected");
                return null;
            }
            double[] coordinate_arr = new double[corner_list.size() - 1];
            for (int i = 0; i < coordinate_arr.length; ++i)
            {
                next_object = it.next();
                if (next_object instanceof Double)
                {
                    coordinate_arr[i] = ((Double) next_object).doubleValue();
                }
                else if (next_object instanceof Integer)
                {
                    coordinate_arr[i] = ((Integer) next_object).intValue();
                }
                else
                {
                    FRLogger.warn("Shape.read_polygon_path_scope: number expected");
                    return null;
                }

            }
            return new PolygonPath(layer, width, coordinate_arr);
        }
        catch (java.io.IOException e)
        {
            FRLogger.error("Shape.read_polygon_path_scope: IO error scanning file", e);
            return null;
        }
    }

    /**
     * Transforms a shape with holes to the board coordinate system.
     * The first shape in the Collection p_area is the border,
     * the other shapes are holes of the area.
     */
    public static app.freerouting.geometry.planar.Area transform_area_to_board(Collection<Shape> p_area, CoordinateTransform p_coordinate_transform)
    {
        int hole_count = p_area.size() - 1;
        if (hole_count <= -1)
        {
            FRLogger.warn("Shape.transform_area_to_board: p_area.size() > 0 expected");
            return null;
        }
        Iterator<Shape> it = p_area.iterator();
        Shape boundary = it.next();
        app.freerouting.geometry.planar.Shape boundary_shape = boundary.transform_to_board(p_coordinate_transform);
        app.freerouting.geometry.planar.Area result;
        if (hole_count == 0)
        {
            result = boundary_shape;
        }
        else
        {
            // Area with holes
            if (!(boundary_shape instanceof app.freerouting.geometry.planar.PolylineShape))
            {
                FRLogger.warn("Shape.transform_area_to_board: PolylineShape expected");
                return null;
            }
            PolylineShape border = (PolylineShape) boundary_shape;
            PolylineShape[] holes = new PolylineShape[hole_count];
            for (int i = 0; i < holes.length; ++i)
            {
                app.freerouting.geometry.planar.Shape hole_shape = it.next().transform_to_board(p_coordinate_transform);
                if (!(hole_shape instanceof PolylineShape))
                {
                    FRLogger.warn("Shape.transform_area_to_board: PolylineShape expected");
                    return null;
                }
                holes[i] = (PolylineShape) hole_shape;
            }
            result = new app.freerouting.geometry.planar.PolylineArea(border, holes);
        }
        return result;
    }

    /**
     * Transforms the relative coordinates of a shape with holes to the board coordinate system.
     * The first shape in the Collection p_area is the border,
     * the other shapes are holes of the area.
     */
    public static app.freerouting.geometry.planar.Area transform_area_to_board_rel(Collection<Shape> p_area, CoordinateTransform p_coordinate_transform)
    {
        int hole_count = p_area.size() - 1;
        if (hole_count <= -1)
        {
            FRLogger.warn("Shape.transform_area_to_board_rel: p_area.size() > 0 expected");
            return null;
        }
        Iterator<Shape> it = p_area.iterator();
        Shape boundary = it.next();
        app.freerouting.geometry.planar.Shape boundary_shape = boundary.transform_to_board_rel(p_coordinate_transform);
        app.freerouting.geometry.planar.Area result;
        if (hole_count == 0)
        {
            result = boundary_shape;
        }
        else
        {
            // Area with holes
            if (!(boundary_shape instanceof app.freerouting.geometry.planar.PolylineShape))
            {
                FRLogger.warn("Shape.transform_area_to_board_rel: PolylineShape expected");
                return null;
            }
            PolylineShape border = (PolylineShape) boundary_shape;
            PolylineShape[] holes = new PolylineShape[hole_count];
            for (int i = 0; i < holes.length; ++i)
            {
                app.freerouting.geometry.planar.Shape hole_shape = it.next().transform_to_board_rel(p_coordinate_transform);
                if (!(hole_shape instanceof PolylineShape))
                {
                    FRLogger.warn("Shape.transform_area_to_board: PolylineShape expected");
                    return null;
                }
                holes[i] = (PolylineShape) hole_shape;
            }
            result = new app.freerouting.geometry.planar.PolylineArea(border, holes);
        }
        return result;
    }

    public void write_hole_scope(IndentFileWriter p_file, IdentifierType p_identifier_type) throws java.io.IOException
    {
        p_file.start_scope();
        p_file.write("window");
        this.write_scope(p_file, p_identifier_type);
        p_file.end_scope();
    }

    /**
     * Transforms a specctra dsn shape to a geometry.planar.Shape.
     */
    public abstract app.freerouting.geometry.planar.Shape transform_to_board(CoordinateTransform p_coordinate_transform);

    /**
     * Returns the smallest axis parallel rectangle containing this shape.
     */
    public abstract Rectangle bounding_box();

    /**
     * Transforms the relative (vector) coordinates of a specctra dsn shape to a geometry.planar.Shape.
     */
    public abstract app.freerouting.geometry.planar.Shape transform_to_board_rel(CoordinateTransform p_coordinate_transform);

    protected Shape(Layer p_layer)
    {
        layer = p_layer;
    }
    public final Layer layer;

    /**
     * Contains the result of the function read_area_scope.
     * area_name or clearance_class_name may be null, which means they are not provided.
     */
    static class ReadAreaScopeResult
    {

        private ReadAreaScopeResult(String p_area_name, Collection<Shape> p_shape_list, String p_clearance_class_name)
        {
            area_name = p_area_name;
            shape_list = p_shape_list;
            clearance_class_name = p_clearance_class_name;
        }
        String area_name; // may be generated later on, if area_name is null.
        final Collection<Shape> shape_list;
        final String clearance_class_name;
    }
}
