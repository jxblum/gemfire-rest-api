# !/usr/bin/env ruby

require 'json'
require 'net/http'

class JsonSerializable

  def to_json
    hash = {}
    hash["@type"] = "org.gopivotal.app.domain.Person"
    self.instance_variables.each do |var|
      if !var.to_s.end_with?("links")
        hash[var.to_s[1..-1]] = self.instance_variable_get var
      end
    end
    hash.to_json
  end

  def from_json! jsonString
    JSON.load(jsonString).each do |var, val|
      if !var.end_with?("type")
        self.instance_variable_set "@".concat(var), val
      end
    end
  end

end

class Person < JsonSerializable

  attr_accessor :id, :firstName, :middleName, :lastName, :birthDate, :gender

  def initialize(id = nil, firstName = nil, middleName = nil, lastName = nil)
    @id = id
    @firstName = firstName
    @middleName = middleName
    @lastName = lastName
    @birthDate = nil
    @gender = nil
  end

  def to_s
    s = "{ type = Person, id = #{@id}"
    s += ", firstName = #{@firstName}"
    s += ", middleName = #{@middleName}"
    s += ", lastName = #{@lastName}"
    s += ", birthDate = #{@birthDate}"
    s += ", gender = #{@gender}"
    s += "}"
  end

end

if __FILE__ == $0
  #p = Person.new(1, "Jon", "T", "Doe")
  #puts p
  #puts p.inspect
  #puts p.to_json

  uri = URI("http://localhost:8080/gemfire-api/v2/People/1");

  personJson = Net::HTTP::get(uri);

  # JSON from server
  puts "JSON read from Server for Person with ID 2...\n #{personJson}"

  p = Person.new
  p.from_json! personJson

  # print the Person to standard out
  puts "Person is...\n #{p}"

  p.id = 1
  p.firstName = "Jack"
  p.lastName = "Handy"
  p.gender = "MALE"

  # prints modified Person to standard out
  puts "Person modified is...\n #{p}"

  puts "JSON sent to Server for Person with ID 2...\n #{p.to_json}"

  Net::HTTP.start(uri.hostname, uri.port) do |http|
    putRequest = Net::HTTP::Put.new uri, { "Content-Type" => "application/json" }
    putRequest.body = p.to_json
    http.request(putRequest)
  end

end
