'use strict';
module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      options: {
        jshintrc: '.jshintrc'
      },
      all: [
        'Gruntfile.js',
        '_assets/js/*.js'
      ]
    },
    recess: {
      dist: {
        options: {
          compile: true,
          compress: true
        },
        files: {
          'css/james.min.css': [
            '_assets/stylesheets/james.less'
          ]
        }
      }
    },
    uglify: {
      dist: {
        files: {
          'js/james.min.js': [
            '_assets/js/james.js'
          ]
        }
      }
    },
    imagemin: {
      dist: {
        options: {
          optimizationLevel: 7,
          progressive: true
        },
        files: [{
          expand: true,
          cwd: 'images/',
          src: '**/*.{png,jpg,jpeg}',
          dest: 'images/'
        }]
      }
    },
    autoprefixer: {
        files: {
          src: 'css/*.css'
        },
    },
    watch: {
      less: {
        files: [
          '_assets/stylesheets/*.less'
        ],
        tasks: ['recess']
      },
      js: {
        files: [
          '<%= jshint.all %>'
        ],
        tasks: ['uglify']
      }
    },
    clean: {
      dist: [
        'css/james.min.css',
        'js/james.min.js'
      ]
    }
  });

  // Load tasks
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-recess');
  grunt.loadNpmTasks('grunt-contrib-imagemin');
  grunt.loadNpmTasks('grunt-autoprefixer');

  // Register tasks
  grunt.registerTask('default', [
    'clean',
    'recess',
    'autoprefixer',
    'uglify',
    'imagemin'
  ]);
  grunt.registerTask('dev', [
    'watch'
  ]);

};