package com.thoughtworks.ketsu.web;

import com.thoughtworks.ketsu.domain.user.CallRecord;
import com.thoughtworks.ketsu.domain.user.CallRecordRepo;
import com.thoughtworks.ketsu.domain.user.User;
import com.thoughtworks.ketsu.domain.user.UserRepo;
import com.thoughtworks.ketsu.web.jersey.Routes;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;

import static com.thoughtworks.ketsu.web.validators.Validators.*;

/**
 * Created by pzzheng on 11/29/16.
 */
public class CallRecordsApi {
    private User user;

    public CallRecordsApi(User user) {
        this.user = user;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCallRecord(Map<String, Object> info,
                                     @Context AuthorizationService authorizationService,
                                     @Context UserRepo userRepo,
                                     @Context CallRecordRepo callRecordRepo,
                                     @Context Routes routes) {
        if (!authorizationService.currentUserIs(user)) {
            throw new NotFoundException("user not exists");
        }

        validate(all(fieldNotEmpty("from_locale", "from_locale is required"),
                fieldNotEmpty("target", "target id is required"),
                fieldNotEmpty("duration", "duration is required")),
                info);
        validate(all(fieldNotEmpty("start", "start time is required"),
                fieldNotEmpty("end", "end time is required")), (Map) info.get("duration"));

        Optional<User> target = userRepo.findBy(info.get("target").toString());
        if(!target.isPresent()) {
            throw new IllegalArgumentException("target must be valid");
        }

        Duration duration = new Duration((long) (((Map) info.get("duration")).get("start")), (long) (((Map) info.get("duration")).get("end")));
        String from_locale = info.get("from_locale").toString();

        CallRecord callerRecord = callRecordRepo.save(new CallRecord(user, target.get(), from_locale, duration));
        callRecordRepo.save(new CallRecord(target.get(), user, from_locale, duration));

        return Response.status(201).location(routes.callRecordsUrl(user.getId().id(), callerRecord.getId().id())).build();
    }


}