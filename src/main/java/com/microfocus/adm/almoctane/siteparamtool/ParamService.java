package com.microfocus.adm.almoctane.siteparamtool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.hpe.adm.nga.sdk.authentication.SimpleUserAuthentication;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.nga.sdk.network.OctaneHttpRequest.GetOctaneHttpRequest;
import com.hpe.adm.nga.sdk.network.OctaneHttpRequest.PutOctaneHttpRequest;
import com.hpe.adm.nga.sdk.network.OctaneHttpResponse;
import com.hpe.adm.nga.sdk.network.google.GoogleHttpClient;

public class ParamService {

    public static List<SiteParam> getParams(String url, String username, String password) {
        url = truncateUrl(url);
        GoogleHttpClient googleHttpClient = getHttpClient(url, username, password);

        GetOctaneHttpRequest req = new GetOctaneHttpRequest(url + "/admin/params" + "?fields=name,value");
        OctaneHttpResponse res = googleHttpClient.execute(req);
        String respString = res.getContent();

        Collection<EntityModel> entities = CustomModelParser.getInstance().getEntities(respString);
        return convertFromEntityCollection(entities);
    }

    public static void saveParams(String url, String username, String password, List<SiteParam> params) {
        url = truncateUrl(url);
        GoogleHttpClient googleHttpClient = getHttpClient(url, username, password);

        Collection<EntityModel> entities = convertToEntityCollection(params);
        String entitiesJson = CustomModelParser.getInstance().getEntitiesJSONObject(entities).toString();

        PutOctaneHttpRequest req = new PutOctaneHttpRequest(url + "/admin/params", "application/json", entitiesJson);
        googleHttpClient.execute(req);
    }

    private static GoogleHttpClient getHttpClient(String url, String username, String password) {
        GoogleHttpClient googleHttpClient = new GoogleHttpClient(truncateUrl(url));
        googleHttpClient.authenticate(new SimpleUserAuthentication(username, password, "HPE_MQM_UI"));
        return googleHttpClient;
    }

    public static String truncateUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String proto = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            return proto + "://" + host + ":" + port;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SiteParam> convertFromEntityCollection(Collection<EntityModel> entityCollection) {
        return entityCollection.stream()
                .map(entityModel -> {
                    String name = entityModel.getValue("id").getValue().toString();
                    String value = null;
                    if (entityModel.getValue("value") != null && entityModel.getValue("value").getValue() != null) {
                        value = entityModel.getValue("value").getValue().toString();
                    }
                    return new SiteParam(name, value);
                })
                .collect(Collectors.toList());
    }

    public static Collection<EntityModel> convertToEntityCollection(List<SiteParam> paramList) {
        return paramList.stream()
                .map(siteParam -> {
                    EntityModel entityModel = new EntityModel();
                    entityModel.setValue(new StringFieldModel("id", siteParam.getParamName()));
                    entityModel.setValue(new StringFieldModel("value", siteParam.getParamValue()));
                    return entityModel;
                })
                .collect(Collectors.toList());
    }

}