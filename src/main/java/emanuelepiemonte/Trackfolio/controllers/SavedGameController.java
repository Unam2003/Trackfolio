package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.entities.SavedGame;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.payload.SavedGameDTO;
import emanuelepiemonte.Trackfolio.services.SavedGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/me/games")
public class SavedGameController {
    private final SavedGameService savedGameService;

    public SavedGameController(SavedGameService savedGameService) {
        this.savedGameService = savedGameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedGame addGame(@AuthenticationPrincipal User currentUser, @RequestBody @Validated SavedGameDTO body) {
        return savedGameService.addToTrackfolio(currentUser, body);
    }

    @PutMapping("/{gameId}")
    public SavedGame updateGame(@AuthenticationPrincipal User currentUser, @PathVariable UUID gameId, @RequestBody @Validated SavedGameDTO body) {
        return this.savedGameService.updateSavedGame(currentUser, gameId, body);
    }


    @GetMapping
    public List<SavedGame> getMyGames(@AuthenticationPrincipal User currentUser) {
        return savedGameService.getMyGames(currentUser);
    }

    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGame(@AuthenticationPrincipal User currentUser, @PathVariable UUID gameId) {
        this.savedGameService.removeFromTrackfolio(currentUser, gameId);
    }

    @GetMapping("/total-hours")
    public Integer getTotalHours(@AuthenticationPrincipal User currentUser) {
        return this.savedGameService.getTotalHours(currentUser);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkGameStatus(@AuthenticationPrincipal User currentUser, @RequestParam Long rawgId) {
        Optional<SavedGame> savedGameOpt = this.savedGameService.findByUserAndRawId(currentUser, rawgId);

        Map<String, Object> response = new HashMap<>();
        if (savedGameOpt.isPresent()) {
            response.put("savedGameId", savedGameOpt.get().getSavedGameId());
            response.put("status", savedGameOpt.get().getStatus());
        } else {
            response.put("savedGameId", null);
        }
        return ResponseEntity.ok(response);
    }

}
