	(function() { // avoid variables ending up in the global scope

	  // page components
	  var playlists,
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);


	  // Page Orchestrator
	  function PageOrchestrator() {

		    this.start = function() {
		      
		    	user_playlist = new UserPlaylist(
		        document.getElementById("id_listcontainer"),
		        document.getElementById("id_listcontainerbody"));

		    };


		    this.refresh = function() {
		     
		      user_playlist.reset();
		    
		    };
		  }

	  
	})();
