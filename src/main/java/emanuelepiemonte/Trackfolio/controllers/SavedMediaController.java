package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.entities.SavedMedia;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.payload.SavedMediaDTO;
import emanuelepiemonte.Trackfolio.services.SavedMediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/me/media")
public class SavedMediaController {
    private SavedMediaService savedMediaService;

    public SavedMediaController(SavedMediaService savedMediaService) {
        this.savedMediaService = savedMediaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedMedia addMedia(@AuthenticationPrincipal User curretUser, @RequestBody @Validated SavedMediaDTO body) {
        return savedMediaService.addToTrackfolio(curretUser, body);
    }

    @PutMapping("/{mediaId}")
    public SavedMedia updateMedia(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID mediaId,
            @RequestBody @Validated SavedMediaDTO body
    ) {
        return this.savedMediaService.updateSavedMedia(currentUser, mediaId, body);
    }

    @GetMapping
    public List<SavedMedia> getMyMedia(@AuthenticationPrincipal User currentUser) {
        return savedMediaService.getMyTrackfolio(currentUser);
    }

    @DeleteMapping("/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMedia(@AuthenticationPrincipal User currentUser, @PathVariable UUID mediaId) {
        this.savedMediaService.removeFromTrackfolio(currentUser, mediaId);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkMedia(@AuthenticationPrincipal User currentUser, @RequestParam Long tmdbId) {
        Optional<SavedMedia> media = savedMediaService.findByTmdbIdAndUser(currentUser, tmdbId);

        if (media.isPresent()) {
            return ResponseEntity.ok(media.get());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("exists", false);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{mediaId}/status")
    public SavedMedia updateStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID mediaId,
            @RequestParam String status) {
        return this.savedMediaService.updateStatus(currentUser, mediaId, status);
    }


}



