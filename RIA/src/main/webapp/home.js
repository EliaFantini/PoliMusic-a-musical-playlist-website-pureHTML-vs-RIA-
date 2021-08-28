(function() { 

	// page components
	var userPlaylists, newSongForm, playlistSongs, songPlayer,
	pageOrchestrator = new PageOrchestrator(); // main controller
		
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);

	document.getElementById("id_newalbum").addEventListener('click', (e) => {
		e.preventDefault(); 
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CreateNewAlbum', e.target.closest("form"),
					function(req) {
				if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText;
					switch (req.status) {
					case 200:
						newSongForm.reset();
						newSongForm.show();
						window.location.href = "#";
						break;
					case 400: // bad request
						document.getElementById("id_errormsgnewalbum").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("id_errormsgnewalbum").textContent = message;
						break;
					case 500: // server error
						document.getElementById("id_errormsgnewalbum").textContent = message;
						break;
					}
				}
			}
			);
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("id_addsong").addEventListener('click', (e) => {
		e.preventDefault(); 
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'AddSongToPlaylist', e.target.closest("form"),
					function(req) {
				if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText;
					switch (req.status) {
					case 200:
						playlistSongs.reset();
						playlistSongs.show(playlistSongs.playlistId);
						window.location.href = "#";
						break;
					case 400: // bad request
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					case 500: // server error
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					}
				}
			}
			);
		} else {
			form.reportValidity();
		}
	});
	
	
	document.getElementById("id_reorder").addEventListener('click', () => {
		playlistSongs.reset();
		playlistSongs.showReorder();
		var elements = document.getElementsByClassName("draggable");
		for (let i = elements.length - 1; i >= 0; i--) {
			elements[i].draggable = true;
			elements[i].addEventListener("dragstart", dragStart);
			elements[i].addEventListener("dragover", dragOver); 
			elements[i].addEventListener("dragleave", dragLeave);
			elements[i].addEventListener("drop", drop);
		}

	});
	
	document.getElementById("id_saveorder").addEventListener('click', () => {
		playlistSongs.saveOrder();
	});

	function unselectSongs(songsArray) {
		for (var i = 0; i < songsArray.length; i++) {
			songsArray[i].className = "notselected";
		}
	}

	function dragStart(event) {
		startSong = event.target.closest("tr");
	}


    function dragOver(event) {
        event.preventDefault(); 
        var destinationSong = event.target.closest("tr");
        // With CSS change selected style
        destinationSong.className = "selected";
    }
    
    function dragLeave(event) {
        var destinationSong = event.target.closest("tr");
        //With CSS change notselected style
        destinationSong.className = "notselected";
    }
    
    function drop(event) {
        
        
        var destinationSong = event.target.closest("tr");
        var table = destinationSong.closest('table'); 
        var songsArray = Array.from(table.querySelectorAll('tbody > tr'));
        var indexDestinationSong = songsArray.indexOf(destinationSong);
        if (songsArray.indexOf(startSong) < indexDestinationSong)       
            startSong.parentElement.insertBefore(startSong, songsArray[indexDestinationSong + 1]);
        else            
            startSong.parentElement.insertBefore(startSong, songsArray[indexDestinationSong]);
        
        unselectSongs(songsArray);
    }

	document.getElementById("id_uploadSong").addEventListener('click', (e) => {
		e.preventDefault(); 
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CreateNewSong', e.target.closest("form"),
					function(req) {
				if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText;
					switch (req.status) {
					case 200:
						playlistSongs.showForm(playlistSongs.playlistId);
						window.location.href = "#";
						break;
					case 400: // bad request
						document.getElementById("id_errormsguploadsong").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("id_errormsguploadsong").textContent = message;
						break;
					case 500: // server error
						document.getElementById("id_errormsguploadsong").textContent = message;
						break;
					}
				}
			}
			);
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("id_newplaylist").addEventListener('click', (e) => {
		e.preventDefault(); 
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CreateNewPlaylist', e.target.closest("form"),
					function(req) {
				if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText;
					switch (req.status) {
					case 200:
						userPlaylists.show();
						window.location.href = "#";
						break;
					case 400: // bad request
						document.getElementById("id_errormsgnewplaylist").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("id_errormsgnewplaylist").textContent = message;
						break;
					case 500: // server error
						document.getElementById("id_errormsgnewplaylist").textContent = message;
						break;
					}
				}
			}
			);
		} else {
			form.reportValidity();
		}
	});

	function UserPlaylists(_listcontainer, _listcontainerbody){

		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.display = "none";
		};

		this.show = function() {
			var self = this;
			makeCall("GET", "GetUserPlaylists", null,
					function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var playlistsToShow = JSON.parse(req.responseText);
						if (playlistsToShow.length == 0) {
							document.getElementById("id_errormsgplaylists").textContent = "No playlists yet!";
							return;
						}
						document.getElementById("id_errormsgplaylists").textContent = "";
						self.update(playlistsToShow); // self visible by closure		           		            
					} else {
						document.getElementById("id_errormsgplaylists").textContent = message;
					}
				}
			}
			);
		};

		this.update = function(arrayPlaylists) {
			var row, namecell, datecell, anchor;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			arrayPlaylists.forEach(function(playlist) { // self visible here, not this
				row = document.createElement("tr");
				namecell = document.createElement("td");
				anchor = document.createElement("a");
				namecell.appendChild(anchor);
				nameText = document.createTextNode(playlist.playlistName);
				anchor.appendChild(nameText);
				anchor.setAttribute('playlistId', playlist.playlistId);
				anchor.addEventListener("click", (e) => {
					playlistSongs.reset();
					playlistSongs.show(e.target.getAttribute("playlistId"));
				}, false);
				anchor.href = "#";
				row.appendChild(namecell);
				datecell = document.createElement("td");
				datecell.textContent = playlist.creationDate;
				row.appendChild(datecell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.display = "block";
		}
	}

	function SongPlayer(_detailscontainer, _detailscontainerbody, _playerAudio){

		this.detailscontainer = _detailscontainer;
		this.detailscontainerbody = _detailscontainerbody;
		this.playerAudio = _playerAudio;

		this.reset = function() {
			this.detailscontainer.style.display = "none";
			this.playerAudio.style.display = "none";
		};

		this.show = function(songId) {
			var songs = playlistSongs.songsToShow, index = playlistSongs.index, l = songs[index].length;
			for(var i = 0; i < l; i++){
				if (songs[index][i].songID == songId){
					this.update(songs[index][i]);
					break;
				}
			}
			
		};

		this.update = function(song) {
			var row, fieldData, fieldText, image, audio, source;
			this.detailscontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			head = self.detailscontainer.children[0];
			head.innerHTML = "";
			
			row = document.createElement("tr");
			fieldData = document.createElement("th");
			fieldText = document.createTextNode(song.songTitle);
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			head.appendChild(row);
			row = document.createElement("tr");
			fieldData = document.createElement("td");
			fieldText = document.createTextNode("Interpreter:");
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			fieldData = document.createElement("td");
			fieldText = document.createTextNode( song.album.interpreter);
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			self.detailscontainerbody.appendChild(row);
			row = document.createElement("tr");
			fieldData = document.createElement("td");
			fieldText = document.createTextNode("Album:");
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			fieldData = document.createElement("td");
			fieldText = document.createTextNode( song.album.albumTitle);
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			self.detailscontainerbody.appendChild(row);
			row = document.createElement("tr");
			fieldData = document.createElement("td");
			fieldText = document.createTextNode("Publication year:" );
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			fieldData = document.createElement("td");
			fieldText = document.createTextNode( song.album.publicationYear);
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			self.detailscontainerbody.appendChild(row);
			row = document.createElement("tr");
			fieldData = document.createElement("td");
			fieldText = document.createTextNode("Genre:");
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			fieldData = document.createElement("td");
			fieldText = document.createTextNode( song.genre);
			fieldData.appendChild(fieldText)
			row.appendChild(fieldData);
			self.detailscontainerbody.appendChild(row);
			

			audio = this.playerAudio;
			source = document.getElementById('audioSrc');
			source.src = song.filePath;
			audio.load();

			this.detailscontainer.style.display = "block";
			this.playerAudio.style.display = "block";
		}


	}

	function PlaylistSongs(_songsDiv, _songscontainer, _songscontainerbody,
			_followingsongs, _previoussongs, _reorder, _saveorder,
			_addsongsform, _songselection){

		this.songsDiv = _songsDiv;
		this.songscontainer = _songscontainer;
		this.songscontainerbody = _songscontainerbody;
		this.followingsongs = _followingsongs;
		this.previoussongs = _previoussongs;
		this.reorder = _reorder;
		this.saveorder = _saveorder;
		this.addsongsform = _addsongsform;
		this.songselection = _songselection;
		this.index = 0;
		this.songsToShow;
		this.playlistId;
		this.songsToOrder;


		this.reset = function() {
			this.songsDiv.style.display = "none";
			this.reorder.style.display = "none";
			this.songscontainer.style.display = "none";
			this.followingsongs.style.display = "none";
			this.saveorder.style.display = "none";
			this.previoussongs.style.display = "none";
			this.addsongsform.style.display = "none";
			this.index = 0;
		};
		
		this.saveOrder = function(){
			var songsArray = Array.from(this.songscontainer.querySelectorAll('tbody > tr'));
			var songIds = new Array();
			songIds.push(this.playlistId)
			for (var i = 0; i < songsArray.length; i++) {
				songIds.push(songsArray[i].getAttribute("songId"))
			}
			makeCallArray("POST", 'ReorderPlaylistSongs', songIds,
					function(req) {
				if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText;
					switch (req.status) {
					case 200:
						playlistSongs.reset();
						playlistSongs.show(playlistSongs.playlistId);
						window.location.href = "#";
						break;
					case 400: // bad request
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					case 401: // unauthorized
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					case 500: // server error
						document.getElementById("id_errormsgplaylistsongs").textContent = message;
						break;
					}
				}
			 }
			);
			
		}
		
		this.showReorder = function(){
			this.songscontainerbody.innerHTML = "";
			var self = this, len = this.songsToShow.length, songs = new Array();
			for (var i = 0; i < len; i++){
				var list = self.songsToShow[i];
				for (var j = 0; j < list.length; j++){
					songs.push(list[j]);
				}
			}
			this.songsToOrder = songs;
			for (var i = 0; i < songs.length; i++){
				row = document.createElement("tr");
				row.className = "draggable";
				row.setAttribute('songId', songs[i].songID);
				namecell = document.createElement("td");
				nameText = document.createTextNode(songs[i].songTitle);
				namecell.appendChild(nameText);
				row.appendChild(namecell);
				self.songscontainerbody.appendChild(row);
			}
			self.songsDiv.style.display = "block";
			self.songscontainer.style.display = "block";
			self.saveorder.style.display = "block";
		}

		this.show = function(playlistID) {
			var self = this;		 
			this.index=0;
			this.playlistId=playlistID;
			document.getElementById("id_playlist").value=playlistID;
			makeCall("GET", "GetPlaylistSongs?playlistID=" + playlistID, null,
					function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						self.songsToShow = JSON.parse(req.responseText);
						self.update(); // self visible by closure
						self.showForm(playlistID);
					}
				} else {
					document.getElementById("id_errormsgplaylistsongs").textContent = message;
				}
			}
			);
		};

		this.update = function() {
			var row, namecell, anchor, image;
			this.songscontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			var i;
			if(self.songsToShow.length>0){
				document.getElementById("id_errormsgplaylistsongs").textContent = "";
				row = document.createElement("tr");
				for(i=0; i<self.songsToShow[this.index].length; i++){
					namecell = document.createElement("td");
					anchor = document.createElement("a");
					namecell.appendChild(anchor);
					nameText = document.createTextNode(self.songsToShow[this.index][i].songTitle);
					anchor.appendChild(nameText);				     
					anchor.setAttribute('songId', self.songsToShow[this.index][i].songID);
					anchor.addEventListener("click", (e) => {
						songPlayer.show(e.target.getAttribute("songId"));
					}, false);
					anchor.href = "#";
					image = document.createElement("td");
					image = document.createElement("img");
					image.src = self.songsToShow[this.index][i].album.imagePath;
					image.alt = "Cover";
					namecell.appendChild(document.createElement("br"));
					namecell.appendChild(document.createElement("br"));
					namecell.appendChild(image);
					row.appendChild(namecell);
				} 
				self.songscontainerbody.appendChild(row);
				self.songscontainer.style.display = "block";
				if(self.songsToShow[this.index].length > 1)
					self.reorder.style.display = "block";
			}
			else {
				document.getElementById("id_errormsgplaylistsongs").textContent = "No songs yet!";
			}
			self.songsDiv.style.display = "block";
			if (self.index < self.songsToShow.length - 1) self.followingsongs.style.display = "block";
			else self.followingsongs.style.display = "none";
			if (self.index > 0) self.previoussongs.style.display = "block";
			else self.previoussongs.style.display = "none";
		};

		this.followingsongs.addEventListener("click", () => {
			this.index = this.index + 1;
			this.update();
		}, false);



		this.previoussongs.addEventListener("click", () => {
			this.index= this.index - 1;
			this.update();
		}, false);


		this.showForm = function(playlistId){
			var self = this;
			makeCall("GET", "GetSongsToAdd?playlistID=" + playlistId, null,
					function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						songsToAdd = JSON.parse(req.responseText);
						self.updateForm(songsToAdd); // self visible by closure
					}
				} else {
					document.getElementById("id_errormsgaddsong").textContent = message;
				}
			}
			);
		};

		this.updateForm = function(arraySongs){
			var option, i, song , len;
			len=this.songselection.length;
			
			for (i=0; i<len; i++) {
				this.songselection.remove(0);
			}
			if (arraySongs.length == 0) {
				this.addsongsform.style.display = "none";
				document.getElementById("id_errormsgaddsong").textContent = "No songs to add!";
			}
			else {
				option = document.createElement( 'option' );
				option.text = "Song";
				option.value = "";
				option.disabled = true;
				option.selected = true;
				this.songselection.appendChild( option );
				for (i=0; i<arraySongs.length; i++){
					song = arraySongs[i];
					option = document.createElement( 'option' );
					option.text = song.songTitle;
					option.value = song.songID;
					this.songselection.appendChild( option );
				}
				this.addsongsform.style.display = "block";
				document.getElementById("id_errormsgaddsong").textContent = "";
			}
		};



	}

	function NewSongForm(_newsongform, _albumselection){

		this.newsongform = _newsongform;
		this.albumselection = _albumselection;

		this.reset = function() {
			this.newsongform.style.display = "none";
		};

		this.show = function() {
			var self = this;
			makeCall("GET", "GetUserAlbums", null,
					function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var albumOptions = JSON.parse(req.responseText);
						if (albumOptions.length == 0) {
							self.newsongform.style.display = "none";
							document.getElementById("id_errormsguploadsong").textContent = "Please create an album first!";
							return;
						}
						document.getElementById("id_errormsguploadsong").textContent = "";
						self.update(albumOptions); // self visible by closure
					}
				} else {
					document.getElementById("id_errormsguploadsong").textContent = message;
				}
			}
			);
		};


		this.update = function(arrayAlbums) {   
			var option, album, i, l = this.albumselection.length;

			for (i=0; i<l; i++) {
				this.albumselection.remove(0);
			}
			option = document.createElement('option');
			option.text = "Album";
			option.value = "";
			option.disabled = true;
			option.selected = true;
			this.albumselection.appendChild( option );
			for (i = 0; i <arrayAlbums.length; i++){
				album = arrayAlbums[i];
				option = document.createElement('option');
				option.text = album.albumTitle;
				option.value = album.albumId;
				this.albumselection.appendChild( option );
			}



			this.newsongform.style.display = "block";
		}



	}

	function PageOrchestrator() {



		this.start = function() {


			userPlaylists = new UserPlaylists(
					document.getElementById("id_playlistscontainer"),
					document.getElementById("id_playlistsbody"));

			newSongForm = new NewSongForm(
					document.getElementById("id_newsongform"),
					document.getElementById("id_albumselection"));

			playlistSongs = new PlaylistSongs(
					document.getElementById("id_songsDiv"),
					document.getElementById("id_songscontainer"),
					document.getElementById("id_songsbody"),
					document.getElementById("id_followingsongs"),
					document.getElementById("id_previoussongs"),
					document.getElementById("id_reorder"),
					document.getElementById("id_saveorder"),
					document.getElementById("id_addsongsform"),
					document.getElementById("id_songselection"));

			songPlayer = new SongPlayer(
					document.getElementById("id_playercontainer"),
					document.getElementById("id_playercontainerbody"),
					document.getElementById('playerAudio'));

		};


		this.refresh = function() {	
			userPlaylists.reset();
			userPlaylists.show();
			newSongForm.reset();
			newSongForm.show();
			playlistSongs.reset();
			songPlayer.reset();
		};
	}


})();
