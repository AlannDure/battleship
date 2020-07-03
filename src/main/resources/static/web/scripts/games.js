var app = new Vue({
    el: '#app',
    data: {
        games: [],
        board: [],
        email: "",
        password: "",
        playerlog: null,


    },
    methods: {
        fillBoard: function () {
            app.games.forEach(game => {
                game.gamePlayers.forEach(gp => {
                    var playerIndex = app.board.findIndex(p => p.username == gp.player.username);
                    if (playerIndex == -1) {

                        var player = {
                            username: "",
                            totalPoints: 0,
                            wins: 0,
                            losses: 0,
                            ties: 0,
                        };

                        player.username = gp.player.username;
                        player.totalPoints = gp.score;

                        if (gp.score == 1.0) {
                            player.wins = 1;

                        } else if (gp.score == 0.5) {
                            player.ties = 1;

                        } else if (gp.score == 0.0) {
                            player.losses = 1;
                        }

                        app.board.push(player);

                    } else {
                        app.board[playerIndex].totalPoints += gp.score;

                        if (gp.score == 1.0) {
                            app.board[playerIndex].wins += 1;

                        } else if (gp.score == 0.5) {
                            app.board[playerIndex].ties += 1;

                        } else if (gp.score == 0.0) {
                            app.board[playerIndex].losses += 1;
                        }

                    }
                    app.board.sort(function (a, b) {
                        if (a.totalPoints === b.totalPoints) {
                            return a.username - b.username
                        } else {
                            return b.totalPoints - a.totalPoints
                        }

                    });
                })
            });
        },
        login: function () {
            $.post("/api/login", {
                    username: app.email,
                    password: app.password,
                })
                .done(function () {
                    alert("Hi 👋");
                    location.reload(true);

                })
                .fail(function () {
                    alert("Something went wrong x_x");
                    location.reload(true);
                })
        },
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    alert("Bye👋");
                    location.reload(true);
                })
        },
        signup: function () {
            $.post("/api/players", {
                    username: app.email,
                    password: app.password,
                })
                .done(function () {
                    alert("Welcome New Player!");
                    app.login();
                })
                .fail(function () {
                    alert("You must complete all the fields in order to sign up");
                    location.reload(true);
                })
        },
        backToGame: function (gamePlayerId) {
            location.href = '/web/game.html?gp=' + gamePlayerId;
        },
        createGame: function () {
            $.post("/api/games")
                .done(function (respuestadeJson) {
                    location.href = '/web/game.html?gp=' + respuestadeJson.gpid;


                })
                .fail(function (error) {
                    alert(error);
                });
        },
        joinGame: function (gameId) {
            $.post("/api/games/" + gameId + "/players")
                .done(function (respuestadeJson) {
                    location.href = '/web/game.html?gp=' + respuestadeJson.gpid;


                });
        }
    }

})


fetch("/api/games")
    .then((response) => {
        return response.json();
    })
    .then(function (myJson) {
        app.games = myJson.games;
        app.playerlog = myJson.player;
        app.fillBoard();
    });