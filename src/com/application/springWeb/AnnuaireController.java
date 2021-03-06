package com.application.springWeb;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.application.beans.Group;
import com.application.beans.Person;
import com.application.business.DaoException;
import com.application.business.PersonDao;
import com.application.metier.AnnuaireValidator;
import com.application.metier.AuthentificationValidator;

@Controller()
@RequestMapping("/annuaire")
@SessionAttributes("user")
public class AnnuaireController {

	@Autowired
	private AnnuaireValidator validator;
	
	@Autowired
	PersonDao personDao;
	
	

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 
	 * @return the home page.
	 *
	 */
	@Autowired 
	AuthentificationValidator authentificationValidator;
	
	/**
	 * 
	 * @return the home page
	 */
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String loginPage(){
		return "accueil";
	}
	/**
	 * 
	 * @param person
	 * @param result
	 * @param modelMap
	 * @param mail
	 * @param password
	 * @return the person whose connected.
	 * this method 
	 */
	@RequestMapping(value="/log", method=RequestMethod.POST)
	public ModelAndView loginVerify( /*HttpServletRequest req, HttpSession session, */
										@ModelAttribute(value="loginPers") Person person, BindingResult result,ModelMap modelMap,
										@RequestParam (value="mail") String mail, @RequestParam (value="password")String password){
		
		
		authentificationValidator.validate(person, result);
		if(result.hasErrors()){
				return new ModelAndView( "accueil");
		}
		String passwordEncrypte=null;
		try {
			passwordEncrypte =  validator.encrypterPasswordPerson(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		person = authentificationValidator.login(mail, passwordEncrypte,person, result);
		if(result.hasErrors()){
			return new ModelAndView( "accueil");
		}
		
		if(!modelMap.containsAttribute("user")) {
		      modelMap.addAttribute("user", person);
		    }
		//session.setAttribute("idPerson", person.getIdPerson());
		
		modelMap.addAttribute("pers", person);
		//modelMap.addAttribute("user", person);
		
		return new ModelAndView( "person");
	}
	
	
	/**
	 * 
	 * @param person
	 * @param request
	 * @param status
	 * @return the home after the user was disconnected and remove the session
	 */
	@RequestMapping(value="logout", method=RequestMethod.GET)
	public String logout(@ModelAttribute("user") Person person, WebRequest request, SessionStatus status){
		//session.removeAttribute("user");
		status.setComplete();
		request.removeAttribute("user", WebRequest.SCOPE_SESSION);
		return "redirect:/";
	}
	
	/**
	 * 
	 * @return the form for add the person in dataBase
	 *
	 */	
	@RequestMapping (value="ajoutform",method=RequestMethod.GET)
	public String register(ModelMap modelMap){
		modelMap.put("pers", new Person());
		return "ajoutPersonForm";
	}
	
	/**
	 * 
	 *  this method verif the data of form and add the person in database
	 * @param person
	 * @param bindingResult
	 * @param modelMap
	 * @return the home page after the person is add in database
	 * @throws DaoException
	 */
	@RequestMapping(value="/addPerson", method=RequestMethod.POST)
	public String register(
			@ModelAttribute(value="pers") Person person, 
			BindingResult bindingResult, ModelMap modelMap) throws DaoException{
			
		//AnnuaireValidator annuaireValidator = new AnnuaireValidator();
		validator.validate(person, bindingResult);
		try {
			person.setPassword(validator.encrypterPasswordPerson(person.getPassword()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bindingResult.hasErrors()){
			//List <FieldError> lst = bindingResult.getFieldErrors();
			return "ajoutPersonForm";
		}else{
			modelMap.put("pers", person);
			modelMap.put("message", "insciption reussi. vous pouvez vous connecter");
			//person.setPassword();
			personDao.savePerson(person);
			return "accueil";
		}
	}
	

	@ModelAttribute("listGroup")
	Collection<Group> groups(){
		
	    return personDao.findAllGroups();
	}
	/**
	 * 
	 * @return return the page which contents the list of group and the list of
	 *         group.
	 * @throws DaoException
	 * 
	 *the list of group will be used in the page returned by this method
	 * 
	 */
	@RequestMapping(value = "/listofGroup", method = RequestMethod.GET)
	public String listGroup(@ModelAttribute("user") Person person, ModelMap modelMap/*, HttpServletRequest req*/) throws DaoException {
		//Person p = (Person) req.getSession().getAttribute("user");
		return "listofGroup";
	}

	/**
	 * return the list of person page
	 * @param req
	 * @param group
	 * @return
	 * 
	 */
	@RequestMapping(value="/listofPerson", method =RequestMethod.GET)
	public ModelAndView listPerson(@ModelAttribute("user") Person person,@RequestParam(value = "idGroup") Long value){
		
		
		Collection<Person> listPerson = null;
		try {
			listPerson = personDao.findAllPersons(value);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		} 
		
		return new ModelAndView("listofPerson", "listPerson", listPerson);
	}
	
	/**
	 *  return the description of person page
	 * @param idPerson
	 * @return
	 * @throws DaoException
	 */
	@RequestMapping(value="/person", method =RequestMethod.GET)
	public ModelAndView personDescription(@RequestParam(value = "idPerson") Long idPerson) throws DaoException{
	
		Person pers = personDao.findPerson(idPerson);
		return new ModelAndView("person", "pers", pers );
	}
	
	/**
	 * return the form for edit a person
	 * @param idPerson
	 * @return
	 * @throws DaoException 
	 */
	@RequestMapping(value="/editPersonForm", method=RequestMethod.GET)
	public ModelAndView editPersonForm(@ModelAttribute("user") Person person, @RequestParam(value="idPerson") Long idPerson) throws DaoException{
		Person pers = personDao.findPerson(idPerson);
		return new ModelAndView("editPersonForm", "pers", pers);
	}
	
	/**
	 * return the desciption fo person page after the person have update his profil.
	 * @param person
	 * @param idPerson
	 * @return
	 * @throws DaoException
	 */
	@RequestMapping(value="/editPerson", method =RequestMethod.POST)
	public String personUpdate(@ModelAttribute("user") Person per,	@ModelAttribute(value="pers") Person person, BindingResult result, ModelMap modelMap) throws DaoException{
		
		//AnnuaireValidator annuaireValidator = new AnnuaireValidator();
		validator.validate(person, result);
		if(result.hasErrors()){
			
			return "editPersonForm";
		}else{
			modelMap.put("pers", person);
			try {
				person.setPassword(validator.encrypterPasswordPerson(person.getPassword()));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			personDao.editPerson(person.getIdPerson(), person);
			return "person";
		}
	}
	
	/**
	 * 
	 * @param person
	 * @param searchValue
	 * @param modelMap
	 * @return the search result
	 */
	@RequestMapping(value="/recherche", method =RequestMethod.GET)
	public String recherche(@ModelAttribute("user") Person person,@RequestParam(value="search") String searchValue, ModelMap modelMap){
		
		Collection <Person> persons = personDao.searchPersonByName(searchValue, searchValue);
		modelMap.addAttribute("listPersonRecherche",persons);
		
		
		Collection <Group> groups = personDao.searchGroupByName(searchValue);
		modelMap.addAttribute("listGroupRecherche", groups);
		
		
		return "ResultatRecherche";
	}
	
	
	/*
	 * password recovery. 
	 */

	/**
	 * 
	 * @param idPerson
	 * @return the page for passwordRecovery
	 */
	@RequestMapping(value="/formPasswordRecovery", method =RequestMethod.GET)
	public String formPasswordRecovery(){
		return "formPasswordRecovery";
	}
	
	@RequestMapping(value="/passwordRecovery", method=RequestMethod.POST)
	public ModelAndView passwordRecovery(@RequestParam(value="mail") String mail, 
										@ModelAttribute(value="recovery") Person person, BindingResult result) throws DaoException{
		
		authentificationValidator.validate(person, result);
		if(result.hasErrors()){
				return new ModelAndView("formPasswordRecovery");
		}
		
		person = authentificationValidator.sendMailPasswordRecovery(mail, person, result);
		if(result.hasErrors()){
			return  new ModelAndView("formPasswordRecovery");
		}
		String message="pour la reinitialisation de votre mot de passe, un mail vous �t� envoy�";
		return new ModelAndView("redirect:/accueil","message", message);
	}
	
	
}
