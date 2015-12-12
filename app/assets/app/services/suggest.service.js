angular
    .module('sproutupApp')
    .factory('SuggestService', SuggestService);

SuggestService.$inject = ['$resource'];

function SuggestService($resource){
	var service = {
		suggestedProducts: suggestedProducts
	};

	return service;

	function suggestedProducts() {
		// return $resource('/api/post/:id', {id:'@id'}, {update:{method:'PUT'}} );

		// dummy suggestedProducts

		var products = [
      {
        'body': 'This product would be rad. Found it on producthunt today.',
        'createdAt': '2015-12-12T03:54:45.201Z',
        'user': {
          'id': 2775,
          'name': 'The Tech Central',
          'nickname': 'thetechcentral',
          'avatarUrl': 'http://d30xksqof6w2my.cloudfront.net/619372bc-6222-4dad-88ca-cce06d0d3560_2775.jpg?w=256&h=256',
          'urlTwitter': 'http://twitter.com/The_TechCentral',
          'handleTwitter': 'The_TechCentral',
          'points': '1000'
        },
        'product': {
            'name': 'Luna 360 camera',
            'url': 'http://luna.camera',
            'description': 'With Luna, a charming little 360° camera, immersive memories are literally one click away.',
        },
        'likes': {
          'data': [
            {
              'id': 747,
              'createdAt': '2015-12-12T03:44:14.368Z',
              'user': {
                'id': 2775,
                'name': 'The Tech Central',
                'nickname': 'thetechcentral',
                'avatarUrl': 'http://d30xksqof6w2my.cloudfront.net/619372bc-6222-4dad-88ca-cce06d0d3560_2775.jpg?w=256&h=256',
                'urlTwitter': 'http://twitter.com/The_TechCentral',
                'handleTwitter': 'The_TechCentral',
                'points': '1000'
              }
            },
            {
              'id': 749,
              'createdAt': '2015-12-12T03:54:45.201Z',
              'user': {
                'id': 828,
                'name': 'Michelle M Lu',
                'nickname': 'michellelu',
                'avatarUrl': 'https://graph.facebook.com/10152932651242820/picture/?type=large',
                'urlTwitter': 'http://twitter.com/_michellehaun',
                'handleTwitter': '_michellehaun',
                'points': '1130'
              }
            }
          ]
        },
        'comments': {
          'data': [
            {
              'id': '513',
              'createdAt': '2015-12-12T07:02:18.833Z',
              'body': 'That dog is so adorable!',
              'user': {
                'id': 575,
                'name': 'Tech Infusion',
                'nickname': 'techinfusion',
                'avatarUrl': 'https://pbs.twimg.com/profile_images/669606886690295808/OWzSFg-Z_bigger.jpg',
                'urlTwitter': 'https://twitter.com/TechInfusion',
                'handleTwitter': 'TechInfusion',
                'points': '3160'
              },
              'likes': {
                'data': [],
                'count': 0
              }
            }
          ],
          'count': '1'
        }  
      },
      {
        'createdAt': '2015-12-12T03:54:45.201Z',
        'user': {
          'id': 2775,
          'name': 'Lord Drac',
          'nickname': 'Drac',
          'avatarUrl': 'http://d30xksqof6w2my.cloudfront.net/619372bc-6222-4dad-88ca-cce06d0d3560_2775.jpg?w=256&h=256',
          'urlTwitter': 'http://twitter.com/The_TechCentral',
          'handleTwitter': 'The_TechCentral',
          'points': '1000'
        },
        'product': {
            'name': 'UBER Christmas',
            'url': 'http://newsroom.uber.com/london/2015/12/treesuk/?ref=producthunt',
            'description': 'With Luna, a charming little 360° camera, immersive memories are literally one click away.',
        },
        'likes': {
          'data': [
            {
              'id': 747,
              'createdAt': '2015-12-12T03:44:14.368Z',
              'user': {
                'id': 2775,
                'name': 'The Tech Central',
                'nickname': 'thetechcentral',
                'avatarUrl': 'http://d30xksqof6w2my.cloudfront.net/619372bc-6222-4dad-88ca-cce06d0d3560_2775.jpg?w=256&h=256',
                'urlTwitter': 'http://twitter.com/The_TechCentral',
                'handleTwitter': 'The_TechCentral',
                'points': '1000'
              }
            },
            {
              'id': 749,
              'createdAt': '2015-12-12T03:54:45.201Z',
              'user': {
                'id': 828,
                'name': 'Michelle M Lu',
                'nickname': 'michellelu',
                'avatarUrl': 'https://graph.facebook.com/10152932651242820/picture/?type=large',
                'urlTwitter': 'http://twitter.com/_michellehaun',
                'handleTwitter': '_michellehaun',
                'points': '1130'
              }
            }
          ]
        },
        'comments': {
          'data': [
            {
              'id': '513',
              'createdAt': '2015-12-12T07:02:18.833Z',
              'body': 'That dog is so adorable!',
              'user': {
                'id': 575,
                'name': 'Tech Infusion',
                'nickname': 'techinfusion',
                'avatarUrl': 'https://pbs.twimg.com/profile_images/669606886690295808/OWzSFg-Z_bigger.jpg',
                'urlTwitter': 'https://twitter.com/TechInfusion',
                'handleTwitter': 'TechInfusion',
                'points': '3160'
              },
              'likes': {
                'data': [],
                'count': 0
              }
            },
            {
              'id': '513',
              'createdAt': '2015-12-30T07:02:18.833Z',
              'body': 'Cool beans.',
              'user': {
                'id': 575,
                'name': 'Nitin',
                'nickname': 'Nitin129',
                'avatarUrl': 'https://pbs.twimg.com/profile_images/669606886690295808/OWzSFg-Z_bigger.jpg',
                'urlTwitter': 'https://twitter.com/TechInfusion',
                'handleTwitter': 'TechInfusion',
                'points': '3160'
              },
              'likes': {
                'data': [],
                'count': 0
              }
            }
          ],
          'count': '1'
        }  
      }
		];

		return products;
	}
}