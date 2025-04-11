package com.example.gameService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping("/games")
    public String getGames() {
        return "Game Service : endpoint /games";
    }
}
