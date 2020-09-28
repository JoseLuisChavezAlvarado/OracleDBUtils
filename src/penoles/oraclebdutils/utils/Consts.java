package penoles.oraclebdutils.utils;

/**
 *
 * @author joseluischavez
 */
public class Consts {

    public static final String RESOURCE_PARENT = "package resources;\n"
            + "\n"
            + "import abstract_classes.ResponseObject;\n"
            + "import security.SecurityUtils;\n"
            + "import com.google.gson.Gson;\n"
            + "import joseluisch.jdbc_utils.database.controller.DatabaseController;\n"
            + "import javax.ws.rs.Consumes;\n"
            + "import javax.ws.rs.DELETE;\n"
            + "import javax.ws.rs.HeaderParam;\n"
            + "import javax.ws.rs.POST;\n"
            + "import javax.ws.rs.PUT;\n"
            + "import javax.ws.rs.Produces;\n"
            + "import javax.ws.rs.core.MediaType;\n"
            + "import javax.ws.rs.core.Response;\n"
            + "\n"
            + "/**\n"
            + " *\n"
            + " * @author Class generated by JDBCUtils.\n"
            + " */\n"
            + "public class ResourceParent<E> {\n"
            + "\n"
            + "    @POST\n"
            + "    @Consumes(MediaType.APPLICATION_JSON)\n"
            + "    @Produces(MediaType.APPLICATION_JSON)\n"
            + "    public Response insert(E object,\n"
            + "            @HeaderParam(\"user\") String user,\n"
            + "            @HeaderParam(\"pass\") String pass,\n"
            + "            @HeaderParam(\"token\") String token) {\n"
            + "\n"
            + "        if (SecurityUtils.validateSecurity(user, pass) || SecurityUtils.validateSecurity(token)) {\n"
            + "\n"
            + "            ResponseObject<Boolean, Exception> responseObject = DatabaseController.insert(object);\n"
            + "            Boolean result = (Boolean) responseObject.getResponse();\n"
            + "            Exception exception = responseObject.getException();\n"
            + "\n"
            + "            if (exception != null) {\n"
            + "                return Response.serverError().entity(new Gson().toJson(exception)).build();\n"
            + "            } else {\n"
            + "                return Response.ok().entity(new Gson().toJson(result)).build();\n"
            + "            }\n"
            + "\n"
            + "        } else {\n"
            + "            return Response.status(Response.Status.FORBIDDEN).build();\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @PUT\n"
            + "    @Consumes(MediaType.APPLICATION_JSON)\n"
            + "    @Produces(MediaType.APPLICATION_JSON)\n"
            + "    public Response update(E object,\n"
            + "            @HeaderParam(\"user\") String user,\n"
            + "            @HeaderParam(\"pass\") String pass,\n"
            + "            @HeaderParam(\"token\") String token) {\n"
            + "\n"
            + "        if (SecurityUtils.validateSecurity(user, pass) || SecurityUtils.validateSecurity(token)) {\n"
            + "\n"
            + "            ResponseObject<Boolean, Exception> responseObject = DatabaseController.update(object);\n"
            + "            Boolean result = (Boolean) responseObject.getResponse();\n"
            + "            Exception exception = responseObject.getException();\n"
            + "\n"
            + "            if (exception != null) {\n"
            + "                return Response.serverError().entity(new Gson().toJson(exception)).build();\n"
            + "            } else {\n"
            + "                return Response.ok().entity(new Gson().toJson(result)).build();\n"
            + "            }\n"
            + "\n"
            + "        } else {\n"
            + "            return Response.status(Response.Status.FORBIDDEN).build();\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @DELETE\n"
            + "    @Consumes(MediaType.APPLICATION_JSON)\n"
            + "    @Produces(MediaType.APPLICATION_JSON)\n"
            + "    public Response delete(E object,\n"
            + "            @HeaderParam(\"user\") String user,\n"
            + "            @HeaderParam(\"pass\") String pass,\n"
            + "            @HeaderParam(\"token\") String token) {\n"
            + "\n"
            + "        if (SecurityUtils.validateSecurity(user, pass) || SecurityUtils.validateSecurity(token)) {\n"
            + "            ResponseObject<Boolean, Exception> responseObject = DatabaseController.delete(object);\n"
            + "            Boolean result = (Boolean) responseObject.getResponse();\n"
            + "            Exception exception = responseObject.getException();\n"
            + "\n"
            + "            if (exception != null) {\n"
            + "                return Response.serverError().entity(new Gson().toJson(exception)).build();\n"
            + "            } else {\n"
            + "                return Response.ok().entity(new Gson().toJson(result)).build();\n"
            + "            }\n"
            + "\n"
            + "        } else {\n"
            + "            return Response.status(Response.Status.FORBIDDEN).build();\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "}\n"
            + "";

    public static final String SECURITY_UTILS = "package security;\n"
            + "\n"
            + "/**\n"
            + " *\n"
            + " * @author Class created By JDBCUtils\n"
            + " */\n"
            + "public class SecurityUtils {\n"
            + "\n"
            + "    public static boolean validateSecurity(String token) {\n"
            + "        boolean result = true;\n"
            + "        //Here´s where your security validation must be coded.\n"
            + "        return result;\n"
            + "    }\n"
            + "\n"
            + "    public static boolean validateSecurity(String user, String pass) {\n"
            + "        boolean result = true;\n"
            + "        //Here´s where your security validation must be coded.\n"
            + "        return result;\n"
            + "    }\n"
            + "\n"
            + "}";

}