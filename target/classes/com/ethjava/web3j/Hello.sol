pragma solidity ^0.4.0;

contract Hello{
    string Hello;

    function set(string hello) public{
        Hello = hello;
    }

    function get() public view returns (string){
        return Hello;

    }
}