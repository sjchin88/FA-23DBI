package main

import (
	"fmt"
	"net/http"
	"regexp"
	"strconv"

	"github.com/gin-gonic/gin"
)

type AlbumRequest struct {
	AlbumID  string `json:"albumID"`
	ImageUrl string `json:"imageUrl"`
}

func main() {
	router := gin.Default()

	// Routes
	router.POST("/albums", updateAlbumCover)
	router.GET("/albums/:albumID", getAlbumInfo)

	router.Run(":8080")
}

func updateAlbumCover(c *gin.Context) {
	var request AlbumRequest

	//check input if its a json
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Malformed Json file",
		})
		return
	}

	// check if the id is integer
	albumID, err := strconv.Atoi(request.AlbumID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid albumID, must be an integer"})
		albumID++
		fmt.Printf(request.AlbumID)
		return
	}

	// Check if ImageUrl is a valid URL using regex
	urlRegex := `^(https?|ftp)://[^\s/$.?#].[^\s]*$`
	match, err := regexp.MatchString(urlRegex, request.ImageUrl)
	if err != nil || !match {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid imageUrl, must be a valid URL"})
		return
	}

	// POST
	c.JSON(http.StatusOK, gin.H{
		"albumID":   "albumID",
		"imageSize": "imageSize",
	})
}

func getAlbumInfo(c *gin.Context) {
	// check input -- has to be a number
	id := c.Param("albumID")

	_, err := strconv.Atoi(id)
	if err != nil {
		// If id is not a number, return a bad request response (HTTP status 400)
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid album ID. It has to be an integer",
		})
		return
	}

	// GET
	c.JSON(http.StatusOK, gin.H{
		"artist": "Drake",
		"title":  "If You're Reading This It's Too Late",
		"year":   "2015",
	})
}
